package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import com.google.gson.Gson;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIRecipeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RecipeService         service;

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

        service.deleteAll();
        userService.deleteAll();

        userService.save( customer );
        userService.save( barista );
        userService.save( manager );
    }

    @Test
    @Transactional
    public void ensureRecipe () throws Exception {
        service.deleteAll();

        final Recipe r = new Recipe();

        r.setPrice( 10 );
        r.setName( "Mocha" );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

    }

    @Test
    @Transactional
    public void testRecipeAPI () throws Exception {

        service.deleteAll();

        final Recipe recipe = new Recipe();
        recipe.setName( "Delicious Not-Coffee" );

        recipe.setPrice( 5 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( recipe ) ) );

        Assertions.assertEquals( 1, (int) service.count() );

    }

    @Test
    @Transactional
    public void testGetRecipeByNameAPI () throws Exception {
        final Gson gson = new Gson();
        final Recipe r = createRecipe( "Pumpkin Spice Latte", 1, 2, 3, 4, 5 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r ) ) ).andExpect( status().isOk() );

        String response = mvc.perform( get( "/api/v1/recipes/Pumpkin Spice Latte" ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();

        final Recipe responseR = gson.fromJson( response, Recipe.class );

        assertEquals( r, responseR );

        // Test getting a non existing recipe
        response = mvc.perform( get( "/api/v1/recipes/dummy" ) ).andExpect( status().is4xxClientError() ).andReturn()
                .getResponse().getContentAsString();

    }

    @Test
    @Transactional
    public void testAddRecipe2 () throws Exception {

        /* Tests a recipe with a duplicate name to make sure it's rejected */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Recipe r1 = createRecipe( name, 50, 3, 1, 1, 0 );

        service.save( r1 );

        final Recipe r2 = createRecipe( name, 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().is4xxClientError() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
    }

    @Test
    @Transactional
    public void testAddRecipe15 () throws Exception {

        /* Tests to make sure that our cap of 3 recipes is enforced */

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee", 50, 3, 1, 1, 0 );
        service.save( r1 );
        final Recipe r2 = createRecipe( "Mocha", 50, 3, 1, 1, 2 );
        service.save( r2 );
        final Recipe r3 = createRecipe( "Latte", 60, 3, 2, 2, 0 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );

        final Recipe r4 = createRecipe( "Hot Chocolate", 75, 0, 2, 1, 2 );

        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r4 ) ) ).andExpect( status().isInsufficientStorage() );

        Assertions.assertEquals( 3, service.count(), "Creating a fourth recipe should not get saved" );
    }

    @Test
    @Transactional
    public void testDeleteRecipe1 () throws Exception {

        /* Testing Deleting one recipe using API */

        // Testing adding and deleting 1 recipe
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee1", 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only be one recipe in the CoffeeMaker" );

        mvc.perform( delete( "/api/v1/recipes/Coffee1" ) ).andExpect( status().isOk() );

        // Testing adding and deleting 2 recipes

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no recipes in the CoffeeMaker" );

        final Recipe r2 = createRecipe( "Coffee2", 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r2 ) ) ).andExpect( status().isOk() );

        final Recipe r3 = createRecipe( "Coffee3", 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r3 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 2, service.findAll().size(), "There should only be two recipes in the CoffeeMaker" );

        mvc.perform( delete( "/api/v1/recipes/Coffee2" ) ).andExpect( status().isOk() );
        mvc.perform( delete( "/api/v1/recipes/Coffee3" ) ).andExpect( status().isOk() );

        // Testing adding and deleting 3 recipes

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no recipes in the CoffeeMaker" );

        final Recipe r4 = createRecipe( "Coffee4", 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r4 ) ) ).andExpect( status().isOk() );

        final Recipe r5 = createRecipe( "Coffee5", 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r5 ) ) ).andExpect( status().isOk() );

        final Recipe r6 = createRecipe( "Coffee6", 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r6 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 3, service.findAll().size(), "There should only be three recipes in the CoffeeMaker" );

        mvc.perform( delete( "/api/v1/recipes/Coffee4" ) ).andExpect( status().isOk() );
        mvc.perform( delete( "/api/v1/recipes/Coffee5" ) ).andExpect( status().isOk() );
        mvc.perform( delete( "/api/v1/recipes/Coffee6" ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no recipes in the CoffeeMaker" );

    }

    @Test
    @Transactional
    public void testConcurrentUsers () throws Exception {

        // Testing deletions of the same recipe by concurrent users

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = createRecipe( "Coffee1", 50, 3, 1, 1, 0 );
        mvc.perform( post( "/api/v1/recipes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( r1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only be one recipe in the CoffeeMaker" );

        mvc.perform( delete( "/api/v1/recipes/Coffee1" ) ).andExpect( status().isOk() );

        mvc.perform( delete( "/api/v1/recipes/Coffee1" ) ).andExpect( status().is4xxClientError() );
    }

    private Recipe createRecipe ( final String name, final Integer price, final Integer coffee, final Integer milk,
            final Integer sugar, final Integer chocolate ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setPrice( price );

        return recipe;
    }

}
