package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Will contain the logic for a customers order
 */
@Entity
@Table ( name = "`Order`" )
public class Order extends DomainObject {

    /** Order id */
    @Id
    @GeneratedValue
    private Long    id = 0L;

    @ManyToOne
    @JoinColumn ( name = "user_id" )
    private User    user;

    private boolean isComplete;

    private boolean isPickedUp;

    @ManyToOne
    @JoinColumn ( name = "recipe_id" )
    private Recipe  recipe;

    public Order () {
        super();
        setUser( null );
        setRecipe( null );

        isComplete = false;
        isPickedUp = false;
    }

    public Order ( final User customer, final Recipe recipe ) {
        this();
        setUser( customer );
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

    public void setUser ( final User user ) {
        this.user = user;
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
    public User getUser () {
        return user;
    }

    /**
     * @return the recipe
     */
    public Recipe getRecipe () {
        return recipe;
    }

}
