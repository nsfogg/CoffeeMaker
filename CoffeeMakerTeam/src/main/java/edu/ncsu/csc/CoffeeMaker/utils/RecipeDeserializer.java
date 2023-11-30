package edu.ncsu.csc.CoffeeMaker.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;

/**
 * This class is used for deserializing JSONObjects instances into Inventory.
 * GSON does not properly serialize or deserialize maps with complex objects as
 * keys which is why this class is necessary. This class is also an
 * Implementation for deserialization of Inventory with Jackson, the SpringBoot
 * JsonParser.
 *
 * Resources referenced: "https://www.baeldung.com/jackson-deserialization",
 * "https://www.baeldung.com/gson-deserialization-guide",
 * "https://www.baeldung.com/gson-list"
 *
 * @author Mohamed Tawous
 */
public class RecipeDeserializer extends StdDeserializer<Recipe> implements JsonDeserializer<Recipe> {

    private static final long serialVersionUID = 1L;

    /**
     * Recipe Deserializer Constructor
     *
     * @param vc
     *            helper class for Recipe Deserializer
     */
    public RecipeDeserializer ( final Class<?> vc ) {
        super( vc );
    }

    /**
     * Recipe Deserializer constructor
     */
    public RecipeDeserializer () {
        this( null );
    }

    @Override
    public Recipe deserialize ( final JsonElement jsonElement, final Type typeOfT,
            final JsonDeserializationContext context ) throws JsonParseException {
        //
        // final JsonObject jsonObject = jsonElement.getAsJsonObject();
        //
        // final Long id = jsonObject.getAsJsonPrimitive( "id" ).getAsLong();
        // final String name = jsonObject.getAsJsonPrimitive( "name"
        // ).getAsString();
        // final Integer price = jsonObject.getAsJsonPrimitive( "price"
        // ).getAsInt();
        //
        // final JsonObject ingredientsJson = jsonObject.getAsJsonObject(
        // "ingredients" );
        //
        // final Map<Ingredient, Integer> ingredients = new HashMap<Ingredient,
        // Integer>();
        //
        // for ( final Map.Entry<String, JsonElement> entry :
        // ingredientsJson.entrySet() ) {
        // // Deserialize the ingredient json into an Ingredient from the entry
        // // key
        // final Ingredient ingredient = new Gson().fromJson( entry.getKey(),
        // Ingredient.class );
        //
        // // Get the amount as int from entry value
        // final Integer amount = entry.getValue().getAsInt();
        //
        // ingredients.put( ingredient, amount );
        // }
        //
        // final Recipe recipe = new Recipe( name, price, ingredients );
        // recipe.setId( id );
        //
        // return recipe;

        return null;
    }

    @Override
    public Recipe deserialize ( final JsonParser jp, final DeserializationContext ctxt )
            throws JsonParseException, JsonProcessingException, IOException {

        final JsonNode node = jp.getCodec().readTree( jp );

        // id may be null (not in the object) so check if it is and if so get it
        // as a long
        final Long id = node.get( "id" ) == null ? null : node.get( "id" ).asLong();

        // Everything else is expected and required. Throw
        // JsonParseException if not found
        if ( node.get( "name" ) == null || node.get( "price" ) == null || node.get( "ingredients" ) == null
                || !node.get( "ingredients" ).isArray() ) {
            throw new JsonParseException( "Recipe is not in the expected format." );
        }

        final String name = node.get( "name" ).asText();
        final Integer price = node.get( "price" ).asInt();
        final ArrayNode ingredients = (ArrayNode) node.get( "ingredients" );

        // We will use this mapper for parsing ingredients from a string.
        final ObjectMapper mapper = new ObjectMapper();

        final Map<Ingredient, Integer> ingredientsMap = new HashMap<Ingredient, Integer>();

        // Parse the next ingredient in the inventory
        for ( final JsonNode ingredientNode : ingredients ) {
            // Parse the ingredient using an object mapper
            final String key = ingredientNode.fieldNames().next();
            final Ingredient ingredient = mapper.readValue( key, Ingredient.class );

            // Parse the amount of the ingredient
            final Integer amount = ingredientNode.get( key ).asInt();

            // Put the key value pair into the ingredients map.
            ingredientsMap.put( ingredient, amount );

        }

        final Recipe recipe = new Recipe( name, price, ingredientsMap );

        if ( id != null ) {
            recipe.setId( id );
        }

        return recipe;

    }
}
