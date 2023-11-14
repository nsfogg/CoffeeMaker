package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Will contain the logic for a customers order
 */
public class Order extends DomainObject {

    /** Order id */
    @Id
    @GeneratedValue
    private Long    id = 0L;

    private Long    customerId;

    private boolean isComplete;

    private boolean isPickedUp;

    private Recipe  recipe;

    public Order () {
        super();
        setCustomerId( 0L );
        setRecipe( null );

        isComplete = false;
        isPickedUp = false;
    }

    public Order ( final Long customer, final Recipe recipe ) {
        this();
        setCustomerId( customer );
        setRecipe( recipe );
        setComplete( false );
        setPickedUp( false );
    }

    public void completeOrder () {
        isComplete = true;
    }

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

    public void setCustomerId ( final Long customerId ) {
        this.customerId = customerId;
    }

    public void setRecipe ( final Recipe recipe ) {
        this.recipe = recipe;
    }

    /**
     * @return the isComplete
     */
    public boolean isComplete () {
        return isComplete;
    }

    /**
     * @param isComplete
     *            the isComplete to set
     */
    public void setComplete ( final boolean isComplete ) {
        this.isComplete = isComplete;
    }

    /**
     * @return the isPickedUp
     */
    public boolean isPickedUp () {
        return isPickedUp;
    }

    /**
     * @param isPickedUp
     *            the isPickedUp to set
     */
    public void setPickedUp ( final boolean isPickedUp ) {
        this.isPickedUp = isPickedUp;
    }

    /**
     * @return the customerId
     */
    public Long getCustomerId () {
        return customerId;
    }

    /**
     * @return the recipe
     */
    public Recipe getRecipe () {
        return recipe;
    }

}
