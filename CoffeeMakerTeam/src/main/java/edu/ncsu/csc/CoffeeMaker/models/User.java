package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.ncsu.csc.CoffeeMaker.utils.UserDeserializer;
import edu.ncsu.csc.CoffeeMaker.utils.UserSerializer;

/**
 * User for the coffee maker. User is tied to the database using Hibernate
 * libraries. See UserRepository and UserService for the other two pieces used
 * for database support.
 *
 * @author Kai Presler-Marshall
 */
@Entity
@JsonDeserialize ( using = UserDeserializer.class )
@JsonSerialize ( using = UserSerializer.class )
public class User extends DomainObject {

    /** User id */
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

    // /**
    // * The ingredients of this recipe and their corresponding amounts.
    // *
    // * Resources referenced:
    // https://www.baeldung.com/hibernate-persisting-maps
    // */
    // @ElementCollection ( fetch = FetchType.EAGER )
    // @CollectionTable ( name = "user_orders", joinColumns = @JoinColumn ( name
    // = "user_id" ) )
    // @MapKeyJoinColumn ( name = "order_id" )
    // @Column ( name = "orders" )
    // @OneToMany ( mappedBy = "user", cascade = CascadeType.ALL, fetch =
    // FetchType.EAGER )
    // private final List<Order> orders = new ArrayList<>();

    /**
     * Default Constructor for User used for guest orders
     *
     */
    public User () {
        super();
        // orders = new ArrayList<Order>();
        setPermissions( 0 );
        setUserName( "annon" );
        setPassword( 0 );
        // this.orders = new ArrayList();
    }

    /**
     * A full constructor that takes everything
     *
     * @param userName
     *            the name of this user
     * @param id
     *            the id of the user
     * @param password
     *            the password of the user
     * @param permissions
     *            the permissions of the user
     */
    public User ( final String userName, final Long id, final String password, final int permissions ) {
        this();
        setUserName( userName );
        setId( id );
        setPassword( hashPassword( password ) );
        setPermissions( permissions );

        // orders = new ArrayList<Order>();
    }

    /**
     * A user constructor that does not take in their unique id
     *
     * @param userName
     *            the name of the user
     * @param password
     *            the password of the user
     * @param perms
     *            the users permission levels
     */
    public User ( final String userName, final String password, final int perms ) {
        this();
        setUserName( userName );
        setPassword( hashPassword( password ) );
        setPermissions( perms );

        // orders = new ArrayList<Order>();
    }

    /**
     * Get the ID of the user
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the user (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Will get the userName
     *
     * @return the userName of the user
     */
    public String getUserName () {
        return userName;
    }

    /**
     * Will get the users permission level
     *
     * @return the permissions of the user
     */
    public Integer getPermissions () {
        return permissions;
    }

    /**
     * Will get the users password
     *
     * @return the hashed password of the user
     */
    public int getPassword () {
        return password;
    }

    /**
     * Will set the users name
     *
     * @param userName
     *            the userName to set
     */
    public void setUserName ( final String userName ) {
        this.userName = userName;
    }

    /**
     * Will set the users permissions
     *
     * @param permissions
     *            the permissions to set
     */
    public void setPermissions ( final Integer permissions ) {
        this.permissions = permissions;
    }

    /**
     * Will set the users password
     *
     * @param password
     *            the password to set
     */
    public void setPassword ( final int password ) {
        this.password = password;
    }

    /**
     * Will hash the users password
     *
     * @param password
     *            the password to set
     * @return the hashed password
     */
    public static int hashPassword ( final String password ) {
        return password.hashCode();
    }

    /**
     * Order object that contains a recipe
     *
     * @param recipe
     *            the recipe for the order
     * @return null if order does not exist, else the order Object
     */
    public Order order ( final Recipe recipe ) {
        final Order ord = new Order( this.id, recipe );
        // orders.add( ord );

        return ord;
    }

    /**
     * Will check the users permission level for manager
     *
     * @return true if the user is the manager. False otherwise
     */
    public boolean isManager () {
        return getPermissions() == 2;
    }

    /**
     * Will check the users permission level for barista
     *
     * @return true if the user is the barista. False otherwise
     */
    public boolean isBarista () {
        return getPermissions() == 1;
    }

    /**
     * Will check the users permission level for customer
     *
     * @return true if the user is the customer. False otherwise
     */
    public boolean isCustomer () {
        return getPermissions() == 0;
    }

}
