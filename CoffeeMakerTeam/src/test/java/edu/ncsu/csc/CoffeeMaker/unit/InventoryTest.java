package edu.ncsu.csc.CoffeeMaker.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class InventoryTest {

    @Autowired
    private InventoryService  inventoryService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private RecipeService     recipeService;

    Ingredient                chocolate = new Ingredient( "Chocolate" );
    Ingredient                vanilla   = new Ingredient( "Vanilla" );
    Ingredient                coffee    = new Ingredient( "Coffee" );
    Ingredient                milk      = new Ingredient( "Milk" );

    List<Ingredient>          ingredients;

    @BeforeEach
    public void setup () {
        inventoryService.deleteAll();
        recipeService.deleteAll();
        ingredientService.deleteAll();
        final Inventory ivt = inventoryService.getInventory();

        // Not sure if we still need to save in ingredient service for this test
        ingredientService.save( chocolate );
        ingredientService.save( vanilla );
        ingredientService.save( coffee );
        ingredientService.save( milk );

        ingredients = ingredientService.findAll();

        final Map<Ingredient, Integer> ingredientMap = new HashMap<Ingredient, Integer>();

        for ( final Ingredient i : ingredients ) {
            ingredientMap.put( i, 500 );
        }

        ivt.updateInventory( ingredientMap );

        inventoryService.save( ivt );
    }

    @Test
    @Transactional
    public void testConsumeInventory () {
        Inventory i = inventoryService.getInventory();

        final Recipe recipe = new Recipe( "Latte", 50 );
        recipe.addIngredient( ingredients.get( 0 ), 5 );
        recipe.addIngredient( ingredients.get( 1 ), 5 );
        recipe.addIngredient( ingredients.get( 2 ), 5 );
        recipe.addIngredient( ingredients.get( 3 ), 5 );

        recipeService.save( recipe );

        assertTrue( i.useIngredients( recipe ) );

        /*
         * Make sure that all of the inventory fields are now properly updated
         */
        for ( final Ingredient k : recipe.getIngredients().keySet() ) {
            Assertions.assertEquals( 495, (int) i.getInventory().get( k ) );
        }

        // Test consume inventory when not enough ingredients
        i = inventoryService.getInventory();

        final Recipe recipe2 = new Recipe();
        recipe2.setName( "Very Costly Recipe" );

        recipe2.setPrice( 10000 );
        recipe2.addIngredient( ingredients.get( 0 ), 5000 );
        recipe2.addIngredient( ingredients.get( 1 ), 5000 );
        recipe2.addIngredient( ingredients.get( 2 ), 5000 );
        recipe2.addIngredient( ingredients.get( 3 ), 5000 );

        assertFalse( i.useIngredients( recipe2 ) );

    }

    @Test
    @Transactional
    public void testUpdateInventory1 () {
        Inventory ivt = inventoryService.getInventory();

        final Map<Ingredient, Integer> ingMap = new HashMap<Ingredient, Integer>();
        // Should still be 500 bc thats how we set it up
        // Adds on top of that a defined integer amount of each ingredient
        for ( final Ingredient i : ingredients ) {
            if ( i.getName().equals( "Milk" ) ) {
                ingMap.put( i, 5 );
            }
            if ( i.getName().equals( "Coffee" ) ) {
                ingMap.put( i, 3 );
            }
            if ( i.getName().equals( "Vanilla" ) ) {
                ingMap.put( i, 7 );
            }
            if ( i.getName().equals( "Chocolate" ) ) {
                ingMap.put( i, 2 );
            }
        }

        ivt.updateInventory( ingMap );

        /* Save and retrieve again to update with DB */
        inventoryService.save( ivt );

        ivt = inventoryService.getInventory();

        for ( final Ingredient k : ingredients ) {
            if ( k.getName().equals( "Milk" ) ) {
                Assertions.assertEquals( 505, (int) ivt.getInventory().get( k ) );
            }
            if ( k.getName().equals( "Coffee" ) ) {
                Assertions.assertEquals( 503, (int) ivt.getInventory().get( k ) );
            }
            if ( k.getName().equals( "Vanilla" ) ) {
                Assertions.assertEquals( 507, (int) ivt.getInventory().get( k ) );
            }
            if ( k.getName().equals( "Chocolate" ) ) {
                Assertions.assertEquals( 502, (int) ivt.getInventory().get( k ) );
            }
        }

    }

    @Test
    @Transactional
    public void testAddInventory2 () {
        final Inventory ivt = inventoryService.getInventory();
        final Map<Ingredient, Integer> ingMap = new HashMap<Ingredient, Integer>();
        for ( final Ingredient i : ingredients ) {
            // if (i.getName().equals("Milk")) {
            // ingMap.put(i, -5);
            // }
            if ( i.getName().equals( "Coffee" ) ) {
                ingMap.put( i, -3 );
            }
            // if (i.getName().equals("Vanilla")) {
            // ingMap.put(i, -7);
            // }
            // if (i.getName().equals("Chocolate")) {
            // ingMap.put(i, -2);
            // }
        }

        try {
            ivt.updateInventory( ingMap );
            inventoryService.save( ivt );

        }
        catch ( final IllegalArgumentException iae ) {

            for ( final Ingredient k : ingredients ) {
                if ( k.getName().equals( "Milk" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for coffee should result in no changes -- milk" );
                }
                if ( k.getName().equals( "Coffee" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for coffee should result in no changes -- coffee" );
                }
                if ( k.getName().equals( "Vanilla" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for coffee should result in no changes -- vanilla" );
                }
                if ( k.getName().equals( "Chocolate" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for coffee should result in no changes -- chocolate" );

                }
            }

        }
    }

    @Test
    @Transactional
    public void testAddInventory3 () {
        final Inventory ivt = inventoryService.getInventory();
        final Map<Ingredient, Integer> ingMap = new HashMap<Ingredient, Integer>();
        for ( final Ingredient i : ingredients ) {
            if ( i.getName().equals( "Milk" ) ) {
                ingMap.put( i, -4 );
            }
        }

        try {
            ivt.updateInventory( ingMap );
            inventoryService.save( ivt );

        }
        catch ( final IllegalArgumentException iae ) {

            for ( final Ingredient k : ingredients ) {
                if ( k.getName().equals( "Milk" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for milk should result in no changes -- milk" );
                }
                if ( k.getName().equals( "Coffee" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for milk should result in no changes -- coffee" );
                }
                if ( k.getName().equals( "Vanilla" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for milk should result in no changes -- vanilla" );
                }
                if ( k.getName().equals( "Chocolate" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for milk should result in no changes -- chocolate" );

                }
            }

        }

    }

    @Test
    @Transactional
    public void testAddInventory4 () {
        final Inventory ivt = inventoryService.getInventory();
        final Map<Ingredient, Integer> ingMap = new HashMap<Ingredient, Integer>();
        for ( final Ingredient i : ingredients ) {
            if ( i.getName().equals( "Chocolate" ) ) {
                ingMap.put( i, -7 );
            }
        }

        try {
            ivt.updateInventory( ingMap );
            inventoryService.save( ivt );

        }
        catch ( final IllegalArgumentException iae ) {

            for ( final Ingredient k : ingredients ) {
                if ( k.getName().equals( "Milk" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- milk" );
                }
                if ( k.getName().equals( "Coffee" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- coffee" );
                }
                if ( k.getName().equals( "Vanilla" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- vanilla" );
                }
                if ( k.getName().equals( "Chocolate" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for chocolate should result in no changes -- chocolate" );

                }
            }

        }

    }

    @Test
    @Transactional
    public void testAddNewIngredientToInventory () {
        final Inventory ivt = inventoryService.getInventory();

        Ingredient pumpkin = new Ingredient( "Pumpkin" );

        ingredientService.save( pumpkin );
        pumpkin = ingredientService.findByName( "Pumpkin" );
        ivt.addNewIngredient( pumpkin, 2 );
        inventoryService.save( ivt );

        ingredients.add( pumpkin );

        for ( final Ingredient k : ingredients ) {
            if ( k.getName().equals( "Milk" ) ) {
                Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ) );
            }
            if ( k.getName().equals( "Coffee" ) ) {
                Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ) );
            }
            if ( k.getName().equals( "Vanilla" ) ) {
                Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ) );
            }
            if ( k.getName().equals( "Chocolate" ) ) {
                Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ) );
            }
            if ( k.getName().equals( "Pumpkin" ) ) {
                Assertions.assertEquals( 2, (int) ivt.getInventory().get( k ) );
            }
        }

    }

    @Test
    @Transactional
    public void testAddInventory5 () {
        final Inventory ivt = inventoryService.getInventory();
        final Map<Ingredient, Integer> ingMap = new HashMap<Ingredient, Integer>();
        for ( final Ingredient i : ingredients ) {
            if ( i.getName().equals( "Vanilla" ) ) {
                ingMap.put( i, -8 );
            }

        }

        try {
            ivt.updateInventory( ingMap );
            inventoryService.save( ivt );

        }
        catch ( final IllegalArgumentException iae ) {

            for ( final Ingredient k : ingredients ) {
                if ( k.getName().equals( "Milk" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for vanilla should result in no changes -- milk" );
                }
                if ( k.getName().equals( "Coffee" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for vanilla should result in no changes -- coffee" );
                }
                if ( k.getName().equals( "Vanilla" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for vanilla should result in no changes -- vanilla" );
                }
                if ( k.getName().equals( "Chocolate" ) ) {
                    Assertions.assertEquals( 500, (int) ivt.getInventory().get( k ),
                            "Trying to update the Inventory with an invalid value for vanilla should result in no changes -- chocolate" );

                }
            }

        }

    }

    @Test
    @Transactional
    public void testToString () {

        final Inventory ivt = new Inventory();

        ivt.addNewIngredient( chocolate, 500 );
        ivt.addNewIngredient( coffee, 500 );
        ivt.addNewIngredient( milk, 500 );
        ivt.addNewIngredient( vanilla, 500 );

        final String testString = "Inventory[\n" + "{Ingredient [id=" + chocolate.getId() + ", name=Chocolate]: 500}\n"
                + "{Ingredient [id=" + coffee.getId() + ", name=Coffee]: 500}\n" + "{Ingredient [id=" + milk.getId()
                + ", name=Milk]: 500}\n" + "{Ingredient [id=" + vanilla.getId() + ", name=Vanilla]: 500}\n" + "]";

        assertEquals( ivt.toString(), testString );

    }

    @Test
    @Transactional
    public void testNotEnoughIngredients () {

        final Inventory ivt = inventoryService.getInventory();

        final Recipe rChoc = new Recipe();
        rChoc.setName( "Lots of Chocolate" );

        rChoc.setPrice( 10 );

        rChoc.addIngredient( chocolate, 600 );

        assertFalse( ivt.enoughIngredients( rChoc ) );

        final Recipe rMilk = new Recipe();
        rChoc.setName( "A Little Milk" );

        rChoc.setPrice( 10 );

        rChoc.addIngredient( milk, 1 );

        assertTrue( ivt.enoughIngredients( rMilk ) );

    }

}
