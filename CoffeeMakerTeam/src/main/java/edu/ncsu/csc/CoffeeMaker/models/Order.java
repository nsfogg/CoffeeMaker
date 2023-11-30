package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

/**
 * Will contain the logic for a customers order
 *
 * @author Nick Fogg, Alexander, and Aliecia
 */
@Entity
@Table ( name = "`Order`" )
public class Order extends DomainObject {

    /** Order id */
    @Id
    @GeneratedValue
    private Long    id = 0L;

    // @ManyToOne
    // @JoinColumn ( name = "user_id" )
    /** The user Id */
    @Column ( name = "user_id" )
    private Long    user;

    /** True if the users order is complete */
    private boolean isComplete;

    /** True if the users order is read to be picked up */
    private boolean isPickedUp;

    /** The recipe for the Order */
    // @ManyToOne
    @JoinColumn ( name = "recipe_id" )
    private String  recipe;

    /**
     * Constructor for an order for an anonymous customer
     */
    public Order () {
        super();
        setUser( 0L );
        setRecipe( null );

        isComplete = false;
        isPickedUp = false;
    }

    /**
     * Constructor for the order object with an associated user and recipe
     *
     * @param customer
     *            the current user
     * @param recipe
     *            the current recipe
     */
    public Order ( final Long customer, final Recipe recipe ) {
        this();
        setUser( customer );
        setRecipe( recipe );
        setComplete( false );
        setPickedUp( false );
    }

    /**
     * Will return true if the current customers order is complete, false if not
     */
    public void completeOrder () {
        isComplete = true;
    }

    /**
     * Will return true if the current customers order is picked up, false if
     * not
     */
    public void pickUpOrder () {
        isPickedUp = true;
    }

    /**
     * Get the ID of the order
     *
     * @return the ID
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Set the ID of the order (Used by Hibernate)
     *
     * @param id
     *            the ID
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Will set the current users id
     *
     * @param user
     *            the current user
     */
    public void setUser ( final Long user ) {
        this.user = user;
    }

    /**
     * Will set the current recipe with the associated customer order
     *
     * @param recipe
     *            the current recipe to set
     */
    public void setRecipe ( final Recipe recipe ) {
        this.recipe = recipe == null ? null : recipe.getName();
    }

    /**
     * Will return true if the current customers order is complete
     *
     * @return the isComplete
     */
    public boolean isComplete () {
        return isComplete;
    }

    /**
     * Will set if the current customers order has been completed
     *
     * @param isComplete
     *            the isComplete to set
     */
    public void setComplete ( final boolean isComplete ) {
        this.isComplete = isComplete;
    }

    /**
     * Will return true if the current customers order is ready to be picked up
     *
     * @return the isPickedUp
     */
    public boolean isPickedUp () {
        return isPickedUp;
    }

    /**
     * Will set the current customers order to be picked up
     *
     * @param isPickedUp
     *            the isPickedUp to set
     */
    public void setPickedUp ( final boolean isPickedUp ) {
        this.isPickedUp = isPickedUp;
    }

    /**
     * Will get the current user
     *
     * @return user the customerId
     */
    public Long getUser () {
        return user;
    }

    /**
     * The orders current recipe
     *
     * @return recipe the recipe
     */
    public String getRecipe () {
        return recipe;
    }

    @Override
    public String toString () {
        return "Order [id=" + id + ", user=" + user + ", isComplete=" + isComplete + ", isPickedUp=" + isPickedUp
                + ", recipe=" + recipe + "]";
    }

}
