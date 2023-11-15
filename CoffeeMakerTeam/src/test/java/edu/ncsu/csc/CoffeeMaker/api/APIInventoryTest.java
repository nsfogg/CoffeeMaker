package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.controllers.DTO.InventoryUserDTO;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIInventoryTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private InventoryService      inventoryService;

    @Autowired
    private IngredientService     ingredientService;

    @Autowired
    private UserService           userService;

    final User                    customer = new User( "customer", "password", 0 );

    final User                    barista  = new User( "barista", "password", 1 );

    final User                    manager  = new User( "manager", "password", 2 );

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        inventoryService.deleteAll();
        ingredientService.deleteAll();
        userService.deleteAll();

        userService.save( customer );
        userService.save( barista );
        userService.save( manager );
    }

    @Test
    @Transactional
    public void testGetInventory () throws Exception {

        // Ensure the inventory is initially empty
        String response = mvc
                .perform( post( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( manager ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        final Inventory responseInventory = TestUtils.asInventory( response );

        assertTrue( responseInventory.getInventory().isEmpty() );

        // Add an Ingredient
        Ingredient i = new Ingredient( "Vanilla" );
        ingredientService.save( i );
        assertEquals( 1, ingredientService.count() );

        // Get the update ingredient from the db
        i = ingredientService.findByName( "Vanilla" );
        responseInventory.addNewIngredient( ingredientService.findByName( "Vanilla" ), 10 );

        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new InventoryUserDTO( responseInventory, manager ) ) ) )
                .andExpect( status().isOk() );

        // Check the changes are reflected in the response from GET
        response = mvc
                .perform( post( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( manager ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        mvc.perform( post( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( customer ) ) ).andExpect( status().isBadRequest() );

        mvc.perform( post( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new User() ) ) ).andExpect( status().isForbidden() );

        assertEquals( 1, responseInventory.getInventory().size() );
        assertTrue( responseInventory.getInventory().containsKey( i ) );
        assertEquals( 10, responseInventory.getInventory().get( i ) );

    }

    @Test
    @Transactional
    public void testUpdateInventory () throws Exception {

        // Ensure the inventory is initially empty
        String response = mvc
                .perform( post( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( manager ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        Inventory responseInventory = TestUtils.asInventory( response );

        assertTrue( responseInventory.getInventory().isEmpty() );

        // Add an Ingredient
        Ingredient vanilla = new Ingredient( "Vanilla" );
        ingredientService.save( vanilla );

        // Get the update ingredient from the db
        vanilla = ingredientService.findByName( "Vanilla" );
        responseInventory.addNewIngredient( vanilla, 10 );

        // Put the new ingredient in the inventory
        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new InventoryUserDTO( responseInventory, manager ) ) ) )
                .andExpect( status().isOk() );

        // Check the changes are reflected in the response from GET
        response = mvc
                .perform( post( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( manager ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        responseInventory = TestUtils.asInventory( response );

        assertFalse( responseInventory.getInventory().isEmpty() );
        assertTrue( responseInventory.getInventory().containsKey( vanilla ) );
        assertEquals( 10, responseInventory.getInventory().get( vanilla ) );

        // Add another Ingredient
        Ingredient chocolate = new Ingredient( "Chocolate" );
        ingredientService.save( chocolate );

        // The inventory object now has chocolate: 5 and vanilla: 10. Since the
        // update endpoint will add new ingredients if they dont exist and sum
        // any existing ingredient amounts together we should end up with.
        // chocolate: 5 and vanilla: 20.

        // Get the update ingredient from the db
        chocolate = ingredientService.findByName( "Chocolate" );
        responseInventory.addNewIngredient( chocolate, 5 );
        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new InventoryUserDTO( responseInventory, manager ) ) ) )
                .andExpect( status().isOk() );

        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new InventoryUserDTO( responseInventory, customer ) ) ) )
                .andExpect( status().isBadRequest() );

        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new InventoryUserDTO( responseInventory, new User() ) ) ) )
                .andExpect( status().isForbidden() );

        // Check the changes are reflected in the response from GET
        response = mvc
                .perform( post( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( manager ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        responseInventory = TestUtils.asInventory( response );

        assertFalse( responseInventory.getInventory().isEmpty() );

        assertTrue( responseInventory.getInventory().containsKey( vanilla ) );
        assertEquals( 20, responseInventory.getInventory().get( vanilla ) );

        assertTrue( responseInventory.getInventory().containsKey( chocolate ) );
        assertEquals( 5, responseInventory.getInventory().get( chocolate ) );

    }

    @Test
    @Transactional
    public void testThrowsExceptionInventory () throws Exception {

        // Ensure the inventory is initially empty
        final String response = mvc
                .perform( post( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( manager ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        final Inventory responseInventory = TestUtils.asInventory( response );

        assertTrue( responseInventory.getInventory().isEmpty() );

        // Add an invalid Ingredient
        final Ingredient i = new Ingredient( "Vanilla" );
        ingredientService.save( i );
        final Map<Ingredient, Integer> inv = responseInventory.getInventory();
        inv.put( i, -10 );
        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new InventoryUserDTO( responseInventory, manager ) ) ) )
                .andExpect( status().is4xxClientError() );

    }
}
