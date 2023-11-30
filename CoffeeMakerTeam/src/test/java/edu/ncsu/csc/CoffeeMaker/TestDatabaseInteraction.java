package edu.ncsu.csc.CoffeeMaker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

/**
 * Tests for interacting with database
 */
@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class TestDatabaseInteraction {

    /**
     * RecipeService for interacting with recipe database
     */
    @Autowired
    private RecipeService recipeService;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        recipeService.deleteAll();
    }

    /**
     * Tests the RecipeService class
     */
    @Test
    @Transactional
    public void testAddRecipes () {

        // Creating a recipe and adding values
        final Recipe r = new Recipe();

        r.setName( "Mocha" );
        r.setPrice( 350 );

        // Save the recipe using the recipe services
        recipeService.save( r );

        // Get a list of the recipes in the recipe service and test for our
        // added recipe
        final List<Recipe> dbRecipes = recipeService.findAll();

        assertEquals( 1, dbRecipes.size() );

        final Recipe dbRecipe = dbRecipes.get( 0 );

        assertEquals( r.getName(), dbRecipe.getName() );

        assertEquals( r.getName(), "Mocha" );
        assertEquals( r.getPrice(), 350 );

        // Test the recipe service find by name functionality
        assertEquals( recipeService.findByName( "Mocha" ), r );

        // Test the edit recipe functionality
        dbRecipe.setPrice( 15 );

        recipeService.save( dbRecipe );

        // Test the recipe list after edit and ensure values are changed
        final List<Recipe> testRecipes = recipeService.findAll();

        assertEquals( testRecipes.size(), 1 );
        assertEquals( testRecipes.get( 0 ), r );

        final Recipe testRecipe = testRecipes.get( 0 );

        assertEquals( testRecipe.getPrice(), 15 );

    }

}
