package edu.ncsu.csc.CoffeeMaker.unit;

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
public class RecipeTest {

    @Autowired
    private RecipeService     service;

    @Autowired
    private IngredientService ingredientService;

    @BeforeEach
    public void setup () {
        service.deleteAll();
    }

    @Test
    @Transactional
    public void testAddRecipe () {

        final Recipe r1 = new Recipe();
        r1.setName( "Black Coffee" );
        r1.setPrice( 1 );

        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );
        r2.setPrice( 1 );

        service.save( r2 );

        final List<Recipe> recipes = service.findAll();
        Assertions.assertEquals( 2, recipes.size(),
                "Creating two recipes should result in two recipes in the database" );

        Assertions.assertEquals( r1, recipes.get( 0 ), "The retrieved recipe should match the created one" );
    }

    @Test
    @Transactional
    public void testNoRecipes () {

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );

        final Recipe r1 = new Recipe();
        r1.setName( "Tasty Drink" );
        r1.setPrice( 12 );

        final Recipe r2 = new Recipe();
        r2.setName( "Mocha" );

        try {
            r2.setPrice( -1 );

            final List<Recipe> recipes = List.of( r1, r2 );

            service.saveAll( recipes );

        }
        catch ( final IllegalArgumentException e ) {
            Assertions.assertEquals( 0, service.count(),
                    "Trying to save a collection of elements where one is invalid should result in neither getting saved" );
        }

    }

    @Test
    @Transactional
    public void testAddRecipe1 () {
        service.deleteAll();
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
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

            ingredientMap.put( i, 3 );

        }

        final Recipe r1 = new Recipe();
        r1.setName( name );
        r1.setPrice( 50 );
        r1.setIngredients( ingredientMap );
        service.save( r1 );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
        Assertions.assertNotNull( service.findByName( name ) );

    }

    /* Test2 is done via the API for different validation */

    @Test
    @Transactional
    public void testAddRecipe3 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Ingredient milk = new Ingredient( "Milk" );
        final List<Ingredient> ingredients = new ArrayList<Ingredient>();

        ingredients.add( milk );
        final Map<Ingredient, Integer> ingredientMap = new HashMap<Ingredient, Integer>();
        for ( final Ingredient i : ingredients ) {
            ingredientMap.put( i, 3 );
        }

        try {
            final Recipe r1 = createRecipe( name, -50, ingredientMap );
            service.save( r1 );
        }
        catch ( final IllegalArgumentException iae ) {
            // expected
            Assertions.assertEquals( "The price of a Recipe must be a positive number", iae.getMessage() );

        }

    }

    @Test
    @Transactional
    public void testAddRecipe4 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Ingredient milk = new Ingredient( "Milk" );
        ingredientService.save( milk );
        final List<Ingredient> ingredients = new ArrayList<Ingredient>();

        ingredients.add( milk );
        final Map<Ingredient, Integer> ingredientMap = new HashMap<Ingredient, Integer>();
        for ( final Ingredient i : ingredients ) {
            ingredientMap.put( i, -3 );
        }

        // NEGATIVE MILK
        try {
            final Recipe r1 = createRecipe( name, 50, ingredientMap );
            service.save( r1 );
        }
        catch ( final IllegalArgumentException iae ) {
            // expected
            Assertions.assertEquals( "Amounts for an ingredient must be positive.", iae.getMessage() );

        }

    }

    @Test
    @Transactional
    public void testAddRecipe5 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Ingredient vanilla = new Ingredient( "Vanilla" );
        ingredientService.save( vanilla );
        final List<Ingredient> ingredients = new ArrayList<Ingredient>();

        ingredients.add( vanilla );
        final Map<Ingredient, Integer> ingredientMap = new HashMap<Ingredient, Integer>();
        for ( final Ingredient i : ingredients ) {
            ingredientMap.put( i, -3 );
        }

        // NEGATIVE vanilla
        try {
            final Recipe r1 = createRecipe( name, 50, ingredientMap );
            service.save( r1 );
        }
        catch ( final IllegalArgumentException iae ) {
            // expected
            Assertions.assertEquals( "Amounts for an ingredient must be positive.", iae.getMessage() );

        }

    }

    @Test
    @Transactional
    public void testAddRecipe6 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Ingredient coffee = new Ingredient( "Coffee" );
        ingredientService.save( coffee );
        final List<Ingredient> ingredients = new ArrayList<Ingredient>();

        ingredients.add( coffee );
        final Map<Ingredient, Integer> ingredientMap = new HashMap<Ingredient, Integer>();
        for ( final Ingredient i : ingredients ) {
            ingredientMap.put( i, -3 );
        }

        // NEGATIVE coffee
        try {
            final Recipe r1 = createRecipe( name, 50, ingredientMap );
            service.save( r1 );
        }
        catch ( final IllegalArgumentException iae ) {
            // expected
            Assertions.assertEquals( "Amounts for an ingredient must be positive.", iae.getMessage() );

        }

    }

    @Test
    @Transactional
    public void testAddRecipe7 () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Chocolate";
        final Ingredient chocolate = new Ingredient( "Chocolate" );
        ingredientService.save( chocolate );
        final List<Ingredient> ingredients = new ArrayList<Ingredient>();

        ingredients.add( chocolate );
        final Map<Ingredient, Integer> ingredientMap = new HashMap<Ingredient, Integer>();
        for ( final Ingredient i : ingredients ) {
            ingredientMap.put( i, -5 );
        }

        // NEGATIVE chocolate
        try {
            final Recipe r1 = createRecipe( name, 50, ingredientMap );
            service.save( r1 );
        }
        catch ( final IllegalArgumentException iae ) {
            // expected
            Assertions.assertEquals( "Amounts for an ingredient must be positive.", iae.getMessage() );

        }

    }

    @Test
    @Transactional
    public void testAddRecipe13 () {
        service.deleteAll();
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final String nameTwo = "Latte";
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
            ingredientMap.put( i, 3 );
        }

        final Recipe r1 = new Recipe();
        r1.setName( name );
        r1.setPrice( 50 );
        r1.setIngredients( ingredientMap );

        final Recipe r2 = new Recipe();
        r2.setName( nameTwo );
        r2.setPrice( 12 );
        r2.setIngredients( ingredientMap );

        r1.updateRecipe( r1 );
        r2.updateRecipe( r2 );

        service.save( r1 );
        service.save( r2 );

        Assertions.assertEquals( 2, service.count(),
                "Creating two recipes should result in two recipes in the database" );

    }

    @Test
    @Transactional
    public void testAddRecipe14 () {
        service.deleteAll();
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final String nameTwo = "Latte";
        final String nameThree = "Frappe";
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
            ingredientMap.put( i, 3 );
        }

        final Recipe r1 = new Recipe();
        r1.setName( name );
        r1.setPrice( 50 );
        r1.setIngredients( ingredientMap );

        final Recipe r2 = new Recipe();
        r2.setName( nameTwo );
        r2.setPrice( 12 );
        r2.setIngredients( ingredientMap );

        final Recipe r3 = new Recipe();
        r3.setName( nameThree );
        r3.setPrice( 13 );
        r3.setIngredients( ingredientMap );

        r1.updateRecipe( r1 );
        r2.updateRecipe( r2 );
        r3.updateRecipe( r3 );

        service.save( r1 );
        service.save( r2 );
        service.save( r3 );

        Assertions.assertEquals( 3, service.count(),
                "Creating three recipes should result in three recipes in the database" );
    }

    @Test
    @Transactional
    public void testValidAddIngToRecipe () {
        service.deleteAll();
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Ingredient chocolate = new Ingredient( "Chocolate" );
        final Ingredient vanilla = new Ingredient( "Vanilla" );
        final Ingredient coffee = new Ingredient( "Coffee" );
        final Ingredient caramel = new Ingredient( "Caramel" );

        ingredientService.save( chocolate );
        ingredientService.save( vanilla );
        ingredientService.save( coffee );
        ingredientService.save( caramel );

        final List<Ingredient> ingredients = ingredientService.findAll();

        final Map<Ingredient, Integer> ingredientMap = new HashMap<Ingredient, Integer>();

        for ( final Ingredient i : ingredients ) {
            ingredientMap.put( i, 3 );
        }

        final Recipe r1 = new Recipe();
        r1.setName( name );
        r1.setPrice( 50 );
        r1.setIngredients( ingredientMap );
        // ADD INGREDIENT
        r1.addIngredient( caramel, 2 );

        r1.updateRecipe( r1 );

        service.save( r1 );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );
        Assertions.assertNotNull( service.findByName( name ) );
    }

    @Test
    @Transactional
    public void testInvalidAddIngToRecipe () {
        service.deleteAll();
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        final String name = "Coffee";
        final Ingredient chocolate = new Ingredient( "Chocolate" );
        final Ingredient vanilla = new Ingredient( "Vanilla" );
        final Ingredient coffee = new Ingredient( "Coffee" );
        final Ingredient caramel = new Ingredient( "Caramel" );
        final List<Ingredient> ingredients = new ArrayList<Ingredient>();

        ingredients.add( coffee );
        ingredients.add( chocolate );
        ingredients.add( vanilla );
        ingredients.add( caramel );

        ingredientService.save( chocolate );
        ingredientService.save( vanilla );
        ingredientService.save( coffee );
        ingredientService.save( caramel );

        final Map<Ingredient, Integer> ingredientMap = new HashMap<Ingredient, Integer>();

        for ( final Ingredient i : ingredients ) {
            ingredientMap.put( i, 3 );
        }

        final Recipe r1 = new Recipe();
        r1.setName( name );
        r1.setPrice( 50 );
        r1.setIngredients( ingredientMap );
        // ADD invalid INGREDIENT amt
        try {
            r1.addIngredient( caramel, -2 );
        }
        catch ( final IllegalArgumentException iae ) {
            Assertions.assertEquals( "The amount for an ingredient must be positive.", iae.getMessage() );
        }

        Assertions.assertEquals( 0, service.findAll().size(), "There should be 0 recipes in the CoffeeMaker" );

    }

    @Test
    @Transactional
    public void testGetRecipe1 () {
        service.deleteAll();
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        ingredientService.deleteAll();
        final String name = "Coffee";
        final Ingredient chocolate = new Ingredient( "Chocolate" );
        final Ingredient vanilla = new Ingredient( "Vanilla" );
        final Ingredient coffee = new Ingredient( "Coffee" );
        final Ingredient caramel = new Ingredient( "Caramel" );

        ingredientService.save( chocolate );
        ingredientService.save( vanilla );
        ingredientService.save( coffee );
        ingredientService.save( caramel );

        final List<Ingredient> ingredients = ingredientService.findAll();

        final Map<Ingredient, Integer> ingredientMap = new HashMap<Ingredient, Integer>();

        for ( final Ingredient i : ingredients ) {
            ingredientMap.put( i, 3 );
        }
        final Recipe r1 = new Recipe();
        r1.setName( name );
        r1.setPrice( 50 );
        r1.setIngredients( ingredientMap );

        service.save( r1 );

        final Recipe retrieved = service.findByName( "Coffee" );

        Assertions.assertEquals( 50, (int) retrieved.getPrice() );
        Assertions.assertEquals( name, retrieved.getName() );
        Assertions.assertEquals( 4, retrieved.getIngredients().size() );

        Assertions.assertEquals( 1, service.count(), "Editing a recipe shouldn't duplicate it" );

    }

    @Test
    @Transactional
    public void testUpdateRecipe () {
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Recipes in the CoffeeMaker" );
        service.deleteAll();
        final String name = "Coffee";
        final String nameTwo = "Latte";
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
            ingredientMap.put( i, 3 );
        }

        final Recipe r1 = new Recipe();
        r1.setName( name );
        r1.setPrice( 50 );
        r1.setIngredients( ingredientMap );
        service.save( r1 );

        final Recipe r2 = new Recipe();
        r2.setName( nameTwo );
        r2.setPrice( 10 );
        r2.setIngredients( ingredientMap );

        r1.updateRecipe( r2 );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only one recipe in the CoffeeMaker" );

    }

    @Test
    @Transactional
    public void testToString () {

        final String name = "Coffee";
        final Ingredient chocolate = new Ingredient( "Chocolate" );

        ingredientService.save( chocolate );

        final List<Ingredient> ingredients = ingredientService.findAll();

        final Map<Ingredient, Integer> ingredientMap = new HashMap<Ingredient, Integer>();

        for ( final Ingredient i : ingredients ) {
            ingredientMap.put( i, 3 );
        }
        final Recipe r1 = new Recipe( name, 50, ingredientMap );
        service.save( r1 );

        assertNotNull( r1.toString() );

    }

    @Test
    @Transactional
    public void testEquals () {

        final String name = "Coffee";
        final Ingredient chocolate = new Ingredient( "Chocolate" );

        ingredientService.save( chocolate );

        final List<Ingredient> ingredients = ingredientService.findAll();
        final Map<Ingredient, Integer> ingredientMap = new HashMap<Ingredient, Integer>();

        for ( final Ingredient i : ingredients ) {
            ingredientMap.put( i, 3 );
        }
        final Recipe r1 = new Recipe();
        r1.setName( name );
        r1.setPrice( 50 );
        r1.setIngredients( ingredientMap );
        service.save( r1 );

        Assertions.assertFalse( r1.equals( null ) );

        final Recipe r2 = new Recipe( name, 10 );
        r2.setIngredients( ingredientMap );
        service.save( r2 );
        final String testString = "tester";
        Assertions.assertFalse( r1.equals( testString ) );

        Assertions.assertTrue( r2.equals( r1 ) );

    }

    private Recipe createRecipe ( final String name, final Integer price, final Map<Ingredient, Integer> ingredients ) {
        final Recipe recipe = new Recipe();
        recipe.setName( name );
        recipe.setIngredients( ingredients );
        recipe.setPrice( price );

        return recipe;
    }

}
