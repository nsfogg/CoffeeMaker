package edu.ncsu.csc.CoffeeMaker.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;

/**
 * This class is used for serializing Recipe instances into JSONObjects. GSON
 * does not properly serialize maps with complex objects as keys which is why
 * this class is necessary.
 *
 * Resources referenced:
 * "https://www.baeldung.com/jackson-custom-serialization",
 * "https://www.baeldung.com/gson-list"
 *
 * @author Mohamed Tawous
 */
public class RecipeSerializer extends StdSerializer<Recipe> implements JsonSerializer<Recipe> {

    private static final long serialVersionUID = 1L;

    /**
     * Recipe Serializer constructor
     *
     * @param t
     *            helper class for Recipe Serializer
     */
    public RecipeSerializer ( final Class<Recipe> t ) {
        super( t );
    }

    /**
     * Recipe Serializer constructor
     */
    public RecipeSerializer () {
        this( null );
    }

    @Override
    public JsonElement serialize ( final Recipe recipe, final Type typeOfSrc, final JsonSerializationContext context ) {

        final JsonObject recipeWrapper = new JsonObject();
        recipeWrapper.addProperty( "id", recipe.getId() );
        recipeWrapper.addProperty( "name", recipe.getName() );
        recipeWrapper.addProperty( "price", recipe.getPrice() );

        final JsonArray ingredients = new JsonArray();
        for ( final Map.Entry<Ingredient, Integer> entry : recipe.getIngredients().entrySet() ) {
            final Ingredient ingredient = entry.getKey();
            final Integer amount = entry.getValue();

            // Serializing an ingredient works automatically with gson.
            final JsonObject ingredientObject = new Gson().toJsonTree( ingredient ).getAsJsonObject();

            final JsonObject ingredientProperty = new JsonObject();
            ingredientProperty.add( ingredientObject.toString(), new JsonPrimitive( amount ) );

            ingredients.add( ingredientProperty );
        }

        recipeWrapper.add( "ingredients", ingredients );

        return recipeWrapper;
    }

    @Override
    public void serialize ( final Recipe recipe, final JsonGenerator gen, final SerializerProvider provider )
            throws IOException {

        gen.writeStartObject();
        gen.writeNumberField( "id", recipe.getId() );
        gen.writeStringField( "name", recipe.getName() );
        gen.writeNumberField( "price", recipe.getPrice() );
        gen.writeArrayFieldStart( "ingredients" );

        final ObjectMapper mapper = new ObjectMapper();
        for ( final Map.Entry<Ingredient, Integer> entry : recipe.getIngredients().entrySet() ) {
            final Ingredient ingredient = entry.getKey();
            final Integer amount = entry.getValue();

            // Serializing an ingredient works automatically with Jackson.
            gen.writeStartObject();
            gen.writeNumberField( mapper.writer().writeValueAsString( ingredient ), amount );
            gen.writeEndObject();

        }

        gen.writeEndArray(); // Close the ingredients array.
        gen.writeEndObject(); // end the json object

    }
}
