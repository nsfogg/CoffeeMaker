package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIOrderTest {

    @Autowired
    private MockMvc           mvc;

    @Autowired
    private RecipeService     recipeService;

    @Autowired
    private InventoryService  inventoryService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private UserService       userService;

    @Autowired
    private OrderService      orderService;

    User                      customer  = new User( "customer", "password", 0 );
    User                      customer2 = new User( "customer2", "password", 0 );

    User                      barista   = new User( "barista", "password", 1 );

    User                      manager   = new User( "manager", "password", 2 );

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
        userService.save( customer2 );
        userService.save( barista );
        userService.save( manager );

        customer = userService.findByName( customer.getUserName() );
        customer2 = userService.findByName( customer2.getUserName() );
        barista = userService.findByName( barista.getUserName() );
        manager = userService.findByName( manager.getUserName() );

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

    @Test
    @Transactional
    public void testOrderBeverage1 () throws Exception {
        final String name = "Latte";

        mvc.perform( post( String.format( "/api/v1/orders/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 60, customer ) ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 10 ) );
        assertEquals( "Latte", ( (Order) orderService.findAll().toArray()[0] ).getRecipe().getName() );
        assertEquals( false, ( (Order) orderService.findAll().toArray()[0] ).isComplete() );
        assertEquals( false, ( (Order) orderService.findAll().toArray()[0] ).isPickedUp() );
        assertEquals( customer.getId(), ( (Order) orderService.findAll().toArray()[0] ).getUser().getId() );

        mvc.perform( post( String.format( "/api/v1/order/status" ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( customer ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 10 ) );

    }

    @Test
    @Transactional
    public void testPurchaseBeverage2 () throws Exception {
        /* Insufficient amount paid */

        final String name = "Latte";

        mvc.perform( post( String.format( "/api/v1/orders/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 40, customer ) ) ) )
                .andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough money paid" ) );

    }

    @Test
    @Transactional
    public void testPurchaseBeverage3 () throws Exception {
        /* No recipe */

        final String name = "jalkdfjlkjfl";

        mvc.perform( post( String.format( "/api/v1/orders/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 60, customer ) ) ) )
                .andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "No recipe selected" ) );

    }

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

        mvc.perform( post( String.format( "/api/v1/orders/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 60, customer ) ) ) )
                .andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough inventory" ) );
    }

    @Test
    @Transactional
    public void testPurchaseBeverage5 () throws Exception {
        // barista ordering
        final String name = "Latte";

        mvc.perform( post( String.format( "/api/v1/orders/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 60, barista ) ) ) )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath( "$.message" ).value( "Only customers can order coffee" ) );

    }

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

    @Test
    @Transactional
    public void testPurchaseBeverage7 () throws Exception {
        // manager ordering
        final String name = "Latte";

        mvc.perform( post( String.format( "/api/v1/orders/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new PaidUserDTO( 60, new User() ) ) ) )
                .andExpect( status().isForbidden() )
                .andExpect( jsonPath( "$.message" ).value( "Current user is not authenticated for this operation" ) );

    }

}
