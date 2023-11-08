package edu.ncsu.csc.CoffeeMaker.utils;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import edu.ncsu.csc.CoffeeMaker.models.User;

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
public class UserSerializer extends StdSerializer<User> implements JsonSerializer<User> {

    private static final long serialVersionUID = 1L;

    /**
     * User Serializer constructor
     *
     * @param t
     *            helper class for User Serializer
     */
    public UserSerializer ( final Class<User> t ) {
        super( t );
    }

    /**
     * User Serializer constructor
     */
    public UserSerializer () {
        this( null );
    }

    @Override
    public JsonElement serialize ( final User user, final Type typeOfSrc, final JsonSerializationContext context ) {

        final JsonObject userWrapper = new JsonObject();
        userWrapper.addProperty( "id", user.getId() );
        userWrapper.addProperty( "userName", user.getUserName() );
        userWrapper.addProperty( "password", user.getPassword() );
        userWrapper.addProperty( "permissions", user.getPermissions() );

        // final JsonArray orders = new JsonArray();
        // for ( final Map.Entry<Ingredient, Integer> entry :
        // recipe.getIngredients().entrySet() ) {
        // final Ingredient ingredient = entry.getKey();
        // final Integer amount = entry.getValue();
        //
        // // Serializing an ingredient works automatically with gson.
        // final JsonObject ingredientObject = new Gson().toJsonTree( ingredient
        // ).getAsJsonObject();
        //
        // final JsonObject ingredientProperty = new JsonObject();
        // ingredientProperty.add( ingredientObject.toString(), new
        // JsonPrimitive( amount ) );
        //
        // ingredients.add( ingredientProperty );
        // }
        //
        // recipeWrapper.add( "ingredients", ingredients );

        return userWrapper;
    }

    @Override
    public void serialize ( final User user, final JsonGenerator gen, final SerializerProvider provider )
            throws IOException {

        gen.writeStartObject();
        gen.writeNumberField( "id", user.getId() );
        gen.writeStringField( "userName", user.getUserName() );
        gen.writeNumberField( "password", user.getPassword() );
        gen.writeNumberField( "permissions", user.getPermissions() );

        // gen.writeArrayFieldStart( "ingredients" );
        //
        // final ObjectMapper mapper = new ObjectMapper();
        // for ( final Map.Entry<Ingredient, Integer> entry :
        // recipe.getIngredients().entrySet() ) {
        // final Ingredient ingredient = entry.getKey();
        // final Integer amount = entry.getValue();
        //
        // // Serializing an ingredient works automatically with Jackson.
        // gen.writeStartObject();
        // gen.writeNumberField( mapper.writer().writeValueAsString( ingredient
        // ), amount );
        // gen.writeEndObject();
        //
        // }
        //
        // gen.writeEndArray(); // Close the ingredients array.
        gen.writeEndObject(); // end the json object

    }
}
