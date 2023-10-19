package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class IngredientTest {

    @Autowired
    private RecipeService     recipeService;

    @Autowired
    private IngredientService ingredientService;
    Ingredient                chocolate;
    Ingredient                vanilla;
    Ingredient                coffee;
    Ingredient                milk;
    Long                      chocolateId;

    @BeforeEach
    public void setup () {
        recipeService.deleteAll();
        ingredientService.deleteAll();
        chocolate = new Ingredient( "Chocolate" );
        vanilla = new Ingredient( "Vanilla" );
        coffee = new Ingredient( "Coffee" );
        milk = new Ingredient( "Milk" );
        final List<Ingredient> ingredients = new ArrayList<Ingredient>();

        ingredients.add( coffee );
        ingredients.add( chocolate );
        ingredients.add( vanilla );
        ingredients.add( milk );

        ingredientService.save( chocolate );
        ingredientService.save( vanilla );
        ingredientService.save( coffee );
        ingredientService.save( milk );

        chocolateId = chocolate.getId();

        final Map<Ingredient, Integer> ingredientMap = new HashMap<Ingredient, Integer>();

        for ( final Ingredient i : ingredients ) {
            ingredientMap.put( i, 3 );
        }

    }

    @Test
    @Transactional
    public void testGetIngredientFields () {
        assertEquals( chocolate.getName(), "Chocolate" );
        assertEquals( chocolate.getId(), chocolateId );

    }

    @Test
    @Transactional
    public void testSetIngredientFields () {
        assertEquals( vanilla.getName(), "Vanilla" );
        vanilla.setName( "Syrup" );
        vanilla.setId( (long) 69 );

        assertEquals( vanilla.getName(), "Syrup" );
        assertEquals( vanilla.getId(), 69 );

        try {
            vanilla.setName( null );

        }
        catch ( final IllegalArgumentException e ) {

            Assertions.assertEquals( "The name of an Ingredient is required", e.getMessage() );

        }

    }

    @Test
    @Transactional
    public void testIngredientEquals () {

        Assertions.assertFalse( chocolate.equals( vanilla ) );
        Assertions.assertFalse( chocolate.equals( null ) );
        Assertions.assertFalse( chocolate.equals( new Recipe() ) );
        Assertions.assertTrue( vanilla.equals( vanilla ) );

    }

    @Test
    @Transactional
    public void testIngredientToString () {
        // TODO: Make this better!
        assertNotNull( chocolate.toString() );
    }

}
