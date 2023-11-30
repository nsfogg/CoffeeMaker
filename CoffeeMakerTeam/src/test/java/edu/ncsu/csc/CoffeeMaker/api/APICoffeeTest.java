package edu.ncsu.csc.CoffeeMaker.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
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

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.controllers.DTO.PaidUserDTO;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 *
 * The APICoffeeTest is responsible for testing making coffee when a user
 * submits a request to do so.
 *
 *
 * @author CSC326 204 Team 1
 *
 */
@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICoffeeTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    @Autowired
    private MockMvc           mvc;

    /**
     * Recipe Service
     */
    @Autowired
    private RecipeService     recipeService;

    /**
     * Inventory Service
     */
    @Autowired
    private InventoryService  inventoryService;

    /**
     * Ingredient Service
     */
    @Autowired
    private IngredientService ingredientService;

    /**
     * User Service
     */
    @Autowired
    private UserService       userService;

    /** Customer User */
    final User                customer = new User( "customer", "password", 0 );

    /** Barista User */
    final User                barista  = new User( "barista", "password", 1 );

    /** Manager User */
    final User                manager  = new User( "manager", "password", 2 );

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        recipeService.deleteAll();
        inventoryService.deleteAll();
        ingredientService.deleteAll();
        userService.deleteAll();

        userService.save( customer );
        userService.save( barista );
        userService.save( manager );

        final Inventory ivt = inventoryService.getInventory();

        final Ingredient chocolate = new Ingredient( "Chocolate" );
        final Ingredient vanilla = new Ingredient( "Vanilla" );
        final Ingredient coffee = new Ingredient( "Coffee" );
        final Ingredient milk = new Ingredient( "Milk" );

        ingredientService.save( chocolate );
        ingredientService.save( vanilla );
        ingredientService.save( coffee );
        ingredientService.save( milk );

        final List<Ingredient> ingredients = ingredientService.findAll();

        final Map<Ingredient, Integer> ingredientMap = new HashMap<Ingredient, Integer>();

        for ( final Ingredient i : ingredients ) {
            ingredientMap.put( i, 15 );
        }

        ivt.updateInventory( ingredientMap );

        inventoryService.save( ivt );

        final Recipe recipe = new Recipe( "Latte", 50 );

        recipe.addIngredient( ingredients.get( 0 ), 1 );
        recipe.addIngredient( ingredients.get( 1 ), 1 );
        recipe.addIngredient( ingredients.get( 2 ), 1 );

        recipeService.save( recipe );
    }

    /**
     * Test successfully purchasing beverage
     *
     * @throws Exception
     *             exception to be thrown when purchase of beverage fails
     */
    @Test
    @Transactional
    public void testPurchaseBeverage1 () throws Exception {
        final String name = "Latte";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 60, customer ) ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 10 ) );

    }

    /**
     * Test failing to purchase beverage due to insufficient amount paid
     *
     * @throws Exception
     *             exception to be thrown when purchase of beverage fails
     */
    @Test
    @Transactional
    public void testPurchaseBeverage2 () throws Exception {
        /* Insufficient amount paid */

        final String name = "Latte";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 40, customer ) ) ) )
                .andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough money paid" ) );

    }

    /**
     * Test failing to purchase beverage due to not selecting a recipe
     *
     * @throws Exception
     *             exception to be thrown when purchase of beverage fails
     */
    @Test
    @Transactional
    public void testPurchaseBeverage3 () throws Exception {
        /* No recipe */

        final String name = "jalkdfjlkjfl";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 60, customer ) ) ) )
                .andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "No recipe selected" ) );

    }

    /**
     * Test failing to purchase beverage due to insufficient inventory items
     *
     * @throws Exception
     *             exception to be thrown when purchase of beverage fails
     */
    @Test
    @Transactional
    public void testPurchaseBeverage4 () throws Exception {
        /* Insufficient inventory */

        final Inventory ivt = inventoryService.getInventory();

        final List<Ingredient> ingredients = ingredientService.findAll();
        final Map<Ingredient, Integer> map = ivt.getInventory();

        // Make the milk ingredient 0
        for ( final Ingredient i : ingredients ) {
            map.put( i, 0 );
        }

        ivt.updateInventory( map );
        inventoryService.save( ivt );

        final String name = "Latte";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 60, customer ) ) ) )
                .andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough inventory" ) );
    }

    /**
     * Test failing to purchase beverage due to not being a customer as barista
     *
     * @throws Exception
     *             exception to be thrown when purchase of beverage fails
     */
    @Test
    @Transactional
    public void testPurchaseBeverage5 () throws Exception {
        // barista ordering
        final String name = "Latte";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 60, barista ) ) ) )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$.message" ).value( "Only customers can order coffee" ) );

    }

    /**
     * Test failing to purchase beverage due to not being a customer as manager
     *
     * @throws Exception
     *             exception to be thrown when purchase of beverage fails
     */
    @Test
    @Transactional
    public void testPurchaseBeverage6 () throws Exception {
        // manager ordering
        final String name = "Latte";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 60, manager ) ) ) )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$.message" ).value( "Only customers can order coffee" ) );

    }

    /**
     * Test failing to purchase beverage due to not being a customer as generic
     * user
     *
     * @throws Exception
     *             exception to be thrown when purchase of beverage fails
     */
    @Test
    @Transactional
    public void testPurchaseBeverage7 () throws Exception {
        // manager ordering
        final String name = "Latte";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 60, new User() ) ) ) )
                .andExpect( status().isForbidden() )
                .andExpect( jsonPath( "$.message" ).value( "Current user is not authenticated for this operation" ) );

    }

}
