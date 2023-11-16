package edu.ncsu.csc.CoffeeMaker.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;

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
public class InventoryDeserializer extends StdDeserializer<Inventory> implements JsonDeserializer<Inventory> {

    private static final long serialVersionUID = 1L;

    /**
     * Inventory Deserializer for JSON use in inventory
     *
     * @param vc
     *            helper class for Inventory Deserializer
     */
    public InventoryDeserializer ( final Class<?> vc ) {
        super( vc );
    }

    /**
     * Inventory Deserializer for JSON use in inventory
     *
     */
    public InventoryDeserializer () {
        this( null );
    }

    @Override
    public Inventory deserialize ( final JsonElement jsonElement, final Type typeOfT,
            final JsonDeserializationContext context ) throws JsonParseException {

        final JsonObject jsonObject = jsonElement.getAsJsonObject();

        // id may be null (not in the object) so check if it is and if so get it
        // as a long
        final Long id = jsonObject.getAsJsonPrimitive( "id" ) == null ? null
                : jsonObject.getAsJsonPrimitive( "id" ).getAsLong();

        final JsonArray inventory = jsonObject.getAsJsonArray( "inventory" );

        final Map<Ingredient, Integer> ingredients = new HashMap<Ingredient, Integer>();

        for ( final JsonElement ingredientEle : inventory ) {
            // Deserialize the ingredient json into an Ingredient from the entry
            // key
            final JsonObject obj = ingredientEle.getAsJsonObject();
            final Entry<String, JsonElement> entry = obj.entrySet().iterator().next();

            final Ingredient ingredient = new Gson().fromJson( entry.getKey(), Ingredient.class );

            // Get the amount as int from entry value
            final Integer amount = entry.getValue().getAsInt();

            ingredients.put( ingredient, amount );
        }

        final Inventory inventoryInstance = new Inventory( ingredients );
        if ( id != null ) {
            inventoryInstance.setId( id );
        }

        return inventoryInstance;
    }

    @Override
    public Inventory deserialize ( final JsonParser jp, final DeserializationContext ctxt )
            throws JsonProcessingException, IOException {

        final JsonNode node = jp.getCodec().readTree( jp );

        // id may be null (not in the object) so check if it is and if so get it
        // as a long
        final Long id = node.get( "id" ) == null ? null : node.get( "id" ).asLong();

        final ArrayNode ingredients = (ArrayNode) node.get( "inventory" );

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

        final Inventory inventoryInstance = new Inventory( ingredientsMap );
        if ( id != null ) {
            inventoryInstance.setId( id );
        }

        return inventoryInstance;

    }
}
