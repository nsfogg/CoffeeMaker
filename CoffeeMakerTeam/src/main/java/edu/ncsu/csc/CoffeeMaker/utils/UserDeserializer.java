package edu.ncsu.csc.CoffeeMaker.utils;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import edu.ncsu.csc.CoffeeMaker.models.User;

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
public class UserDeserializer extends StdDeserializer<User> implements JsonDeserializer<User> {

    private static final long serialVersionUID = 1L;

    /**
     * Recipe Deserializer Constructor
     *
     * @param vc
     *            helper class for Recipe Deserializer
     */
    public UserDeserializer ( final Class< ? > vc ) {
        super( vc );
    }

    /**
     * Recipe Deserializer constructor
     */
    public UserDeserializer () {
        this( null );
    }

    @Override
    public User deserialize ( final JsonElement jsonElement, final Type typeOfT,
            final JsonDeserializationContext context ) throws JsonParseException {

        return null;
    }

    @Override
    public User deserialize ( final JsonParser jp, final DeserializationContext ctxt )
            throws JsonParseException, JsonProcessingException, IOException {

        final JsonNode node = jp.getCodec().readTree( jp );

        // id may be null (not in the object) so check if it is and if so get it
        // as a long
        final Long id = node.get( "id" ) == null ? null : node.get( "id" ).asLong();

        // Everything else is expected and required. Throw
        // JsonParseException if not found
        if ( node.get( "userName" ) == null || node.get( "password" ) == null || node.get( "permissions" ) == null ) {
            throw new JsonParseException( "User is not in the expected format." );
        }

        final String userName = node.get( "userName" ).asText();
        final Integer password = node.get( "password" ).asInt();
        final Integer permissions = node.get( "permissions" ).asInt();

        // // We will use this mapper for parsing ingredients from a string.
        // final ObjectMapper mapper = new ObjectMapper();
        //
        // final Map<Ingredient, Integer> ingredientsMap = new
        // HashMap<Ingredient, Integer>();
        //
        // // Parse the next ingredient in the inventory
        // for ( final JsonNode ingredientNode : ingredients ) {
        // // Parse the ingredient using an object mapper
        // final String key = ingredientNode.fieldNames().next();
        // final Ingredient ingredient = mapper.readValue( key, Ingredient.class
        // );
        //
        // // Parse the amount of the ingredient
        // final Integer amount = ingredientNode.get( key ).asInt();
        //
        // // Put the key value pair into the ingredients map.
        // ingredientsMap.put( ingredient, amount );
        //
        // }

        final User user = new User();
        user.setUserName( userName );
        user.setPassword( password );
        user.setPermissions( permissions );

        if ( id != null ) {
            user.setId( id );
        }

        return user;

    }
}
