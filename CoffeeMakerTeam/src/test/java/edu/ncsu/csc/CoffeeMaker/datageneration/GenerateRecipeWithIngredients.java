package edu.ncsu.csc.CoffeeMaker.datageneration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.DomainObject;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class GenerateRecipeWithIngredients {

    @Autowired
    private RecipeService    recipeService;

    @Autowired
    private InventoryService intventoryService;

    @Autowired
    IngredientService        ingredientService;

    @Test
    // @Transactional
    public void createRecipes () {

        // All recipes and inventory entries containing an association to an
        // ingredient must be removed before the ingredient is removed
        recipeService.deleteAll();
        intventoryService.deleteAll();
        ingredientService.deleteAll();

        createIngredients();

        Recipe r1 = new Recipe( "Delicious Coffee", 50 );
        Recipe r2 = new Recipe( "Delicious Latte", 200 );

        final List<Ingredient> ingredients = ingredientService.findAll();

        r1.addIngredient( ingredients.get( 0 ), 23 );
        r1.addIngredient( ingredients.get( 1 ), 27 );
        recipeService.save( r1 );

        r2.addIngredient( ingredients.get( 0 ), 200 );
        r2.addIngredient( ingredients.get( 1 ), 100 );
        recipeService.save( r2 );

        printRecipes();

        assertEquals( 2, recipeService.count() );

        final List<Recipe> recipes = recipeService.findAll();

        r1 = recipes.get( 0 );
        r2 = recipes.get( 1 );

        assertTrue( r1.getIngredients().containsKey( ingredients.get( 0 ) ) );
        assertTrue( r1.getIngredients().containsKey( ingredients.get( 1 ) ) );
        assertTrue( r2.getIngredients().containsKey( ingredients.get( 0 ) ) );
        assertTrue( r2.getIngredients().containsKey( ingredients.get( 1 ) ) );

        assertEquals( 23, r1.getIngredients().get( ingredients.get( 0 ) ) );
        assertEquals( 27, r1.getIngredients().get( ingredients.get( 1 ) ) );
        assertEquals( 200, r2.getIngredients().get( ingredients.get( 0 ) ) );
        assertEquals( 100, r2.getIngredients().get( ingredients.get( 1 ) ) );
    }

    private void printRecipes () {
        for ( final DomainObject r : recipeService.findAll() ) {
            System.out.println( r );
        }
    }

    public void createIngredients () {
        ingredientService.deleteAll();

        final Ingredient i1 = new Ingredient( "Chocolate Syrup" );

        ingredientService.save( i1 );

        final Ingredient i2 = new Ingredient( "Pumpkin Spice" );

        ingredientService.save( i2 );

    }

}
