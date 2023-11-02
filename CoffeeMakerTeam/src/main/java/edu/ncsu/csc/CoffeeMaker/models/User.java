package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.ncsu.csc.CoffeeMaker.utils.RecipeDeserializer;
import edu.ncsu.csc.CoffeeMaker.utils.RecipeSerializer;

/**
 * Recipe for the coffee maker. Recipe is tied to the database using Hibernate
 * libraries. See RecipeRepository and RecipeService for the other two pieces
 * used for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
@JsonDeserialize ( using = RecipeDeserializer.class )
@JsonSerialize ( using = RecipeSerializer.class )
public class User extends DomainObject {

    /** Recipe id */
    @Id
    @GeneratedValue
    private Long    id = 0L;

    /** user name */
    private String  userName;

    /** user permissions */
    @Min ( 0 )
    private Integer permissions;

    /** hashed password for user */
    private int     password;

    /**
     * The ingredients of this recipe and their corresponding amounts.
     *
     * Resources referenced: https://www.baeldung.com/hibernate-persisting-maps
     */
    // @ElementCollection ( fetch = FetchType.EAGER )
    // @CollectionTable ( name = "recipe_ingredients", joinColumns = @JoinColumn
    // ( name = "recipe_id" ) )
    // @MapKeyJoinColumn ( name = "ingredient_id" )
    // @Column ( name = "orders" )
    // private List orders;

    /**
     * Default Constructor for Recipe (Used by Hibernate)
     *
     */
    public User () {
        super();
        setPermissions( 0 );
        setUserName( "annon" );
        setPassword( 0 );
        // this.orders = new ArrayList();
    }

    /**
     * A minimalist constructor that takes only the required fields of a Recipe.
     *
     * @param name
     *            the name of this recipe
     * @param price
     *            the price of this recipe
     */
    public User ( final String userName, final Long id, final String password, final int perms ) {
        this();
        setUserName( userName );
        setId( id );
        setPassword( hashPassword( password ) );
        setPermissions( permissions );
    }

    /**
     * Get the ID of the Recipe
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the Recipe (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * @return the userName
     */
    public String getUserName () {
        return userName;
    }

    /**
     * @return the permissions
     */
    private Integer getPermissions () {
        return permissions;
    }

    /**
     * @return the password
     */
    public int getPassword () {
        return password;
    }

    /**
     * @param userName
     *            the userName to set
     */
    private void setUserName ( final String userName ) {
        this.userName = userName;
    }

    /**
     * @param permissions
     *            the permissions to set
     */
    private void setPermissions ( final Integer permissions ) {
        this.permissions = permissions;
    }

    /**
     * @param password
     *            the password to set
     */
    private void setPassword ( final int password ) {
        this.password = password;
    }

    /**
     * @param password
     *            the password to set
     */
    public int hashPassword ( final String password ) {
        return password.hashCode();
    }

    /**
     *
     * @param recipe
     */
    public Order order ( final Recipe recipe ) {
        return null;
    }

    /**
     *
     * @return
     */
    public boolean isManager () {
        return getPermissions() == 2;
    }

    /**
     *
     * @return
     */
    public boolean isBarista () {
        return getPermissions() == 1;
    }

    /**
     *
     * @return
     */
    public boolean isCustomer () {
        return getPermissions() == 0;
    }

}
