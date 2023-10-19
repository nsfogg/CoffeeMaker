package edu.ncsu.csc.CoffeeMaker.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.utils.InventoryDeserializer;
import edu.ncsu.csc.CoffeeMaker.utils.InventorySerializer;
import edu.ncsu.csc.CoffeeMaker.utils.RecipeDeserializer;
import edu.ncsu.csc.CoffeeMaker.utils.RecipeSerializer;

/**
 * Class for handy utils shared across all of the API tests
 *
 * @author Kai Presler-Marshall
 *
 */
public class TestUtils {

    // Build the GSON instance with inventory serializer and deserializer
    public static Gson gson = new GsonBuilder().registerTypeAdapter( Inventory.class, new InventorySerializer( null ) )
            .registerTypeAdapter( Inventory.class, new InventoryDeserializer() )
            .registerTypeAdapter( Recipe.class, new RecipeSerializer() )
            .registerTypeAdapter( Recipe.class, new RecipeDeserializer() ).create();

    /**
     * Uses Google's GSON parser to serialize a Java object to JSON. Useful for
     * creating JSON representations of our objects when calling API methods.
     *
     * @param obj
     *            to serialize to JSON
     * @return JSON string associated with object
     */
    public static String asJsonString ( final Object obj ) {
        return gson.toJson( obj );
    }

    public static Inventory asInventory ( final String response ) {
        return gson.fromJson( response, Inventory.class );
    }

    public static Recipe asRecipe ( final String response ) {
        return gson.fromJson( response, Recipe.class );
    }

}
