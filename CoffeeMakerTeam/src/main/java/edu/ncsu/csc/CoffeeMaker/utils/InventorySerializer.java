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
import edu.ncsu.csc.CoffeeMaker.models.Inventory;

/**
 * This class is used for serializing Inventory instances into JSONObjects. GSON
 * does not properly serialize maps with complex objects as keys which is why
 * this class is necessary.
 *
 * Resources referenced:
 * "https://www.baeldung.com/jackson-custom-serialization",
 * "https://www.baeldung.com/gson-list"
 *
 * @author Mohamed Tawous
 */
public class InventorySerializer extends StdSerializer<Inventory> implements JsonSerializer<Inventory> {

    private static final long serialVersionUID = 1L;

    /**
     * Inventory Serializer for JSON use in inventory
     *
     * @param t
     *            helper class for Inventory Deserializer
     */
    public InventorySerializer ( final Class<Inventory> t ) {
        super( t );
    }

    /**
     * Inventory Serializer for JSON use in inventory
     *
     */
    public InventorySerializer () {
        this( null );
    }

    @Override
    public JsonElement serialize ( final Inventory inventory, final Type typeOfSrc,
            final JsonSerializationContext context ) {

        final JsonObject inventoryWrapper = new JsonObject();
        inventoryWrapper.addProperty( "id", inventory.getId() );

        final JsonArray inventoryArr = new JsonArray();
        final Gson gson = new Gson();
        for ( final Map.Entry<Ingredient, Integer> entry : inventory.getInventory().entrySet() ) {
            final Ingredient ingredient = entry.getKey();
            final Integer amount = entry.getValue();

            // Serializing an ingredient works automatically with gson.
            final JsonObject ingredientObject = new JsonObject();
            ingredientObject.add( gson.toJsonTree( ingredient ).getAsJsonObject().toString(),
                    new JsonPrimitive( amount ) );

            inventoryArr.add( ingredientObject );
        }

        inventoryWrapper.add( "inventory", inventoryArr );

        return inventoryWrapper;
    }

    @Override
    public void serialize ( final Inventory inventory, final JsonGenerator gen, final SerializerProvider provider )
            throws IOException {

        gen.writeStartObject();
        gen.writeNumberField( "id", inventory.getId() );

        gen.writeArrayFieldStart( "inventory" );

        final ObjectMapper mapper = new ObjectMapper();
        for ( final Map.Entry<Ingredient, Integer> entry : inventory.getInventory().entrySet() ) {
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
