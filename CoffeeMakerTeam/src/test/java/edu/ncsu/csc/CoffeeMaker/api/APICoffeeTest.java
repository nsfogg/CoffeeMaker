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
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@ExtendWith ( SpringExtension.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APICoffeeTest {

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

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        recipeService.deleteAll();
        inventoryService.deleteAll();
        ingredientService.deleteAll();
        userService.deleteAll();

        final User customer = new User( "customer", "password", 0 );
        userService.save( customer );
        final User barista = new User( "barista", "password", 1 );
        userService.save( barista );
        final User manager = new User( "manager", "password", 2 );
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

    @Test
    @Transactional
    public void testPurchaseBeverage1 () throws Exception {
        final String name = "Latte";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 60 ) ) ).andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( 10 ) );

    }

    @Test
    @Transactional
    public void testPurchaseBeverage2 () throws Exception {
        /* Insufficient amount paid */

        final String name = "Latte";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 40 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough money paid" ) );

    }

    @Test
    @Transactional
    public void testPurchaseBeverage3 () throws Exception {
        /* Insufficient inventory */

        final Inventory ivt = inventoryService.getInventory();

        final List<Ingredient> ingredients = ingredientService.findAll();
        final Map<Ingredient, Integer> map = ivt.getInventory();

        // Make the milk ingredient 0
        for ( final Ingredient i : ingredients ) {
            if ( i.getName().equals( "Milk" ) ) {
                map.put( i, 0 );
            }
        }

        ivt.updateInventory( map );
        inventoryService.save( ivt );

        final String name = "Latte";

        mvc.perform( post( String.format( "/api/v1/makecoffee/%s", name ) ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( 30 ) ) ).andExpect( status().is4xxClientError() )
                .andExpect( jsonPath( "$.message" ).value( "Not enough money paid" ) );

    }

}
