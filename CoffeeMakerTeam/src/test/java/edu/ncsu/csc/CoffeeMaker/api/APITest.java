package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.controllers.DTO.InventoryUserDTO;
import edu.ncsu.csc.CoffeeMaker.controllers.DTO.PaidUserDTO;
import edu.ncsu.csc.CoffeeMaker.controllers.DTO.RecipeUserDTO;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Test for comprehensive API flow
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APITest {

    /**
     * context for web application
     */
    @Autowired
    private WebApplicationContext context;

    /**
     * MockMvc for testing http requests
     */
    @Autowired
    private MockMvc               mvc;

    /**
     * RecipeService for interacting with recipe database
     */
    @Autowired
    private RecipeService         recipeService;

    /**
     * InventoryService for interacting with inventory database
     */
    @Autowired
    private InventoryService      inventoryService;

    /**
     * IngredientService for interacting with ingredient database
     */
    @Autowired
    private IngredientService     ingredientService;

    /**
     * UserService for interacting with user database
     */
    @Autowired
    private UserService           userService;

    /** tests user for customer */
    final User                    customer = new User( "customer", "password", 0 );

    /**
     * Test user for barista
     */
    final User                    barista  = new User( "barista", "password", 1 );

    /**
     * test user for manager
     */
    final User                    manager  = new User( "manager", "password", 2 );

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        recipeService.deleteAll();
        inventoryService.deleteAll();
        ingredientService.deleteAll();
        userService.deleteAll();

        userService.save( customer );
        userService.save( barista );
        userService.save( manager );
    }

    /**
     * Ensures that the recipe has the correct fields, inventory starts empty,
     * and ingredients are added correctly
     *
     * @throws Exception
     *             if any of the recipe requirements, user, or inventory is
     *             incorrect after performing API calls
     */
    @Test
    @Transactional
    public void ensureRecipe () throws Exception {

        // Ensure the inventory is initially empty
        final String response = mvc
                .perform( get( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                        .param( "userName", manager.getUserName() )
                        .param( "password", Integer.toString( manager.getPassword() ) )
                        .content( TestUtils.asJsonString( manager ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Learned how to parse json strings to objects here
        final Inventory responseInventory = TestUtils.asInventory( response );

        assertTrue( responseInventory.getInventory().isEmpty() );

        // Add an Ingredient
        final Ingredient i = new Ingredient( "Vanilla" );
        ingredientService.save( i );
        final Ingredient savedI = ingredientService.findByName( "Vanilla" );
        responseInventory.addNewIngredient( savedI, 10 );

        assertNotNull( responseInventory );

        // Put the inventory
        mvc.perform( put( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new InventoryUserDTO( responseInventory, manager ) ) ) )
                .andExpect( status().isOk() );

        assertEquals( 1, responseInventory.getInventory().size() );

        assertFalse( responseInventory.getInventory().isEmpty() );
        assertTrue( responseInventory.getInventory().containsKey( i ) );
        assertTrue( responseInventory.getInventory().containsValue( 10 ) );

        String recipe = mvc
                .perform( get( "/api/v1/recipes/" ).contentType( MediaType.APPLICATION_JSON )
                        .param( "userName", manager.getUserName() )
                        .param( "password", Integer.toString( manager.getPassword() ) )
                        .content( TestUtils.asJsonString( manager ) ) )
                .andDo( print() ).andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        if ( !recipe.contains( "Mocha" ) ) {
            final Recipe r = new Recipe();
            r.setName( "Mocha" );
            r.setPrice( 100 );
            r.addIngredient( savedI, 2 );

            mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                    .content( TestUtils.asJsonString( new RecipeUserDTO( r, manager ) ) ) )
                    .andExpect( status().isOk() );

        }

        recipe = mvc
                .perform( get( "/api/v1/recipes/" ).contentType( MediaType.APPLICATION_JSON )
                        .param( "userName", manager.getUserName() )
                        .param( "password", Integer.toString( manager.getPassword() ) )
                        .content( TestUtils.asJsonString( manager ) ) )
                .andDo( print() ).andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        assertTrue( recipe.contains( "Mocha" ) );

        // Make coffee
        mvc.perform( post( "/api/v1/makecoffee/Mocha" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 100, customer ) ) ) ).andExpect( status().isOk() );

        // Ensure the inventory is initially empty
        final String response1 = mvc
                .perform( get( "/api/v1/inventory" ).contentType( MediaType.APPLICATION_JSON )
                        .param( "userName", manager.getUserName() )
                        .param( "password", Integer.toString( manager.getPassword() ) )
                        .content( TestUtils.asJsonString( manager ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Learned how to parse json strings to objects here
        final Inventory responseInventory1 = TestUtils.asInventory( response1 );

        assertFalse( responseInventory1.getInventory().isEmpty() );
        assertTrue( responseInventory1.getInventory().containsKey( savedI ) );
        assertEquals( 8, responseInventory1.getInventory().get( savedI ) );

    }

}
