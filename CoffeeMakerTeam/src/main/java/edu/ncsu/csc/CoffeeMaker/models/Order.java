package edu.ncsu.csc.CoffeeMaker.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import edu.ncsu.csc.CoffeeMaker.utils.UserDeserializer;
import edu.ncsu.csc.CoffeeMaker.utils.UserSerializer;

/**
 * Will contain the logic for a customers order
 */
@Entity
@JsonDeserialize ( using = UserDeserializer.class )
@JsonSerialize ( using = UserSerializer.class )
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

        isComplete = false;
        isPickedUp = false;
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

}
