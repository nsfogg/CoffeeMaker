package edu.ncsu.csc.CoffeeMaker.datageneration;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class GenerateIngredients {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private RecipeService     recipeService;

    @Autowired
    private InventoryService  intventoryService;

    @Test
    // @Transactional
    public void testCreateIngredients () {

        // All recipes and inventory entries containing an association to an
        // ingredient must be removed before the ingredient is removed
        recipeService.deleteAll();
        intventoryService.deleteAll();
        ingredientService.deleteAll();

        final Ingredient i1 = new Ingredient( "Chocolate Syrup" );

        ingredientService.save( i1 );

        final Ingredient i2 = new Ingredient( "Pumpkin Spice" );

        ingredientService.save( i2 );

        Assert.assertEquals( 2, ingredientService.count() );

    }
}
