package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
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

import com.google.gson.reflect.TypeToken;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIIngredientTest {

    // Deleting an ingredient that has recipes associates, deletes
    // ingredients from everywhere

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private IngredientService     ingredientService;

    @Autowired
    private InventoryService      inventoryService;

    @Autowired
    private RecipeService         rService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        rService.deleteAll();
        inventoryService.deleteAll();
        ingredientService.deleteAll();

    }

    @Test
    @Transactional
    public void testIngredientAPI () throws Exception {

        final Ingredient i = new Ingredient( "Vanilla" );

        mvc.perform( post( "/api/v1/ingredients" ).queryParam( "amount", "10" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i ) ) )
                .andExpect( status().isOk() );

    }

    @Test
    @Transactional
    public void testGetIngredientByNameAPI () throws Exception {

        final Ingredient i = new Ingredient( "Vanilla" );

        mvc.perform( post( "/api/v1/ingredients" ).queryParam( "amount", "10" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i ) ) )
                .andExpect( status().isOk() );

        String response = mvc.perform( get( "/api/v1/ingredients/Vanilla" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        final Ingredient responseI = TestUtils.gson.fromJson( response, Ingredient.class );

        assertEquals( "Vanilla", responseI.getName() );

        // Test getting a non existing ingredient
        response = mvc.perform( get( "/api/v1/ingredients/dummy" ) ).andExpect( status().is4xxClientError() )
                .andReturn().getResponse().getContentAsString();

        System.out.println( response );

    }

    @Test
    @Transactional
    public void testAddIngredientDuplicate () throws Exception {

        /*
         * Tests a ingredient with a duplicate name to make sure it's rejected
         */

        Assertions.assertEquals( 0, ingredientService.count(), "There should be no Ingredients in the CoffeeMaker" );
        final Ingredient i1 = new Ingredient( "Vanilla" );

        ingredientService.save( i1 );
        assertEquals( i1, ingredientService.findByName( "Vanilla" ) );

        final Ingredient i2 = new Ingredient( "Vanilla" );
        mvc.perform( post( "/api/v1/ingredients" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, ingredientService.count(), "There should only one ingredient in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testAddMultipleIngredient () throws Exception {

        Assertions.assertEquals( 0, ingredientService.count(), "There should be no Ingredients in the CoffeeMaker" );

        final Ingredient i1 = new Ingredient( "Vanilla" );

        mvc.perform( post( "/api/v1/ingredients" ).queryParam( "amount", "10" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i1 ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, ingredientService.count() );
        assertNotNull( ingredientService.findByName( "Vanilla" ) );

        final Ingredient i2 = new Ingredient( "Chocolate" );
        mvc.perform( post( "/api/v1/ingredients" ).queryParam( "amount", "10" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i2 ) ) )
                .andExpect( status().isOk() );

        assertNotNull( ingredientService.findByName( "Chocolate" ) );
        Assertions.assertEquals( 2, ingredientService.count() );
    }

    @Test
    @Transactional
    public void testDeleteIngredient () throws Exception {

        /* Testing Deleting one recipe using API */

        // Testing adding and deleting 1 ingredient
        Assertions.assertEquals( 0, ingredientService.count(), "There should be no Ingredients in the CoffeeMaker" );

        final Ingredient i1 = new Ingredient( "Vanilla" );
        mvc.perform( post( "/api/v1/ingredients" ).queryParam( "amount", "200" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i1 ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, ingredientService.count(),
                "There should only be one ingredient in the CoffeeMaker" );

        mvc.perform( delete( "/api/v1/ingredients/Vanilla" ) ).andExpect( status().isOk() );

        // Testing adding and deleting 2 ingredients

        Assertions.assertEquals( 0, ingredientService.count(), "There should be no ingredients in the CoffeeMaker" );

        final Ingredient i2 = new Ingredient( "Sugar" );
        mvc.perform( post( "/api/v1/ingredients" ).queryParam( "amount", "200" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i2 ) ) )
                .andExpect( status().isOk() );

        final Ingredient i3 = new Ingredient( "Caramel" );
        mvc.perform( post( "/api/v1/ingredients" ).queryParam( "amount", "10" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i3 ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 2, ingredientService.count(),
                "There should only be two ingredients in the CoffeeMaker" );

        mvc.perform( delete( "/api/v1/ingredients/Sugar" ) ).andExpect( status().isOk() );
        mvc.perform( delete( "/api/v1/ingredients/Caramel" ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 0, ingredientService.count(), "There should be no recipes in the CoffeeMaker" );

    }

    @Test
    @Transactional
    public void testDeleteIngredientWithRecipe () throws Exception {

        Assertions.assertEquals( 0, ingredientService.count(), "There should be no Ingredients in the CoffeeMaker" );

        final Ingredient i1 = new Ingredient( "Vanilla" );
        final Ingredient i2 = new Ingredient( "Chocolate" );

        mvc.perform( post( "/api/v1/ingredients" ).queryParam( "amount", "200" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i1 ) ) )
                .andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/ingredients" ).queryParam( "amount", "200" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i2 ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 2, ingredientService.count(),
                "There should only be two ingredients in the CoffeeMaker" );

        Recipe r1 = new Recipe( "Coffee", 100 );
        Recipe r2 = new Recipe( "Mocha", 100 );

        r1.addIngredient( ingredientService.findByName( "Vanilla" ), 10 );
        r2.addIngredient( ingredientService.findByName( "Chocolate" ), 10 );
        rService.save( r1 );
        rService.save( r2 );

        assertEquals( 2, rService.count() );

        // Deleting an ingredient should delete it system wide which means it
        // will be deleted from any recipes that have it.
        mvc.perform( delete( "/api/v1/ingredients/Vanilla" ) ).andExpect( status().isOk() );

        // Testing adding and deleting 2 ingredients
        Assertions.assertEquals( 1, ingredientService.count(), "There should be one ingredient in the CoffeeMaker" );

        // Ingredient should have been deleted from Coffee, but we should still
        // have one in Mocha
        r1 = rService.findByName( "Coffee" );
        r2 = rService.findByName( "Mocha" );

        assertEquals( 0, r1.getIngredients().size() );
        assertEquals( 1, r2.getIngredients().size() );

    }

    @Test
    @Transactional
    public void testGetIngredients () throws Exception {

        Assertions.assertEquals( 0, ingredientService.count(), "There should be no Ingredients in the CoffeeMaker" );

        final Ingredient i1 = new Ingredient( "Vanilla" );
        mvc.perform( post( "/api/v1/ingredients" ).queryParam( "amount", "200" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i1 ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, ingredientService.count(),
                "There should only be one ingredient in the CoffeeMaker" );

        final String response = mvc.perform( get( "/api/v1/ingredients" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        final Type listType = new TypeToken<ArrayList<Ingredient>>() {
        }.getType();

        final List<Ingredient> ingredients = TestUtils.gson.fromJson( response, listType );

        assertEquals( 1, ingredients.size() );
        assertEquals( i1, ingredients.get( 0 ) );

    }

    @Test
    @Transactional
    public void testCreateSameNameIngredient () throws Exception {

        Assertions.assertEquals( 0, ingredientService.count(), "There should be no Ingredients in the CoffeeMaker" );

        final Ingredient i1 = new Ingredient( "Vanilla" );
        mvc.perform( post( "/api/v1/ingredients" ).queryParam( "amount", "200" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i1 ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, ingredientService.count(),
                "There should only be one ingredient in the CoffeeMaker" );

        final Ingredient i2 = new Ingredient( "Vanilla" );
        mvc.perform( post( "/api/v1/ingredients" ).queryParam( "amount", "200" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i2 ) ) )
                .andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, ingredientService.count(),
                "There should only be one ingredient in the CoffeeMaker" );

    }

    @Test
    @Transactional
    public void testUpdateIngredient () throws Exception {

        // Testing adding and deleting 1 ingredient
        Assertions.assertEquals( 0, ingredientService.count(), "There should be no Ingredients in the CoffeeMaker" );

        Ingredient i1 = new Ingredient( "Vanilla" );
        mvc.perform( post( "/api/v1/ingredients" ).queryParam( "amount", "200" )
                .contentType( MediaType.APPLICATION_JSON ).content( TestUtils.asJsonString( i1 ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 1, ingredientService.count(),
                "There should only be one ingredient in the CoffeeMaker" );

        i1 = ingredientService.findByName( "Vanilla" );
        final Ingredient i2 = new Ingredient( "Caramel" );

        mvc.perform( put( "/api/v1/ingredients/Vanilla" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, ingredientService.count(),
                "There should only be one ingredient in the CoffeeMaker" );

        assertNotNull( ingredientService.findByName( "Caramel" ) );
        assertEquals( i1, ingredientService.findByName( "Caramel" ) );

        mvc.perform( put( "/api/v1/ingredients/FakeIngredient" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( i2 ) ) ).andExpect( status().is4xxClientError() );

    }

}