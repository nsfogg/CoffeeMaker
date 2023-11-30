package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.Order;

/**
 * The OrderUserDTO provides logic to communicate the order object with its user
 */
public class OrderUserDTO {

    /**
     * Order id
     */
    public Long          id = 0L;

    /**
     * User who made the order
     */
    public final Long    user;

    /**
     * Is the order complete
     */
    public final boolean isComplete;

    /**
     * Is the order picked up
     */
    public final boolean isPickedUp;

    /**
     * Name of recipe for the order
     */
    public final String  recipe;

    /**
     * Username of the user for the order
     */
    public final String  userName;

    /**
     * Will return the OrderUserDTO for the order
     *
     * @param order
     *            the order object
     * @param userName
     *            the username of the user associated with the order
     */
    public OrderUserDTO ( final Order order, final String userName ) {

        this.id = order.getId();
        this.user = order.getUser();
        this.isComplete = order.isComplete();
        this.isPickedUp = order.isPickedUp();
        this.recipe = order.getRecipe();
        this.userName = userName;
    }

    /**
     * Will return the OrderUserDTO for the order
     */
    public OrderUserDTO () {
        this.id = 0L;
        this.user = 0L;
        this.isComplete = false;
        this.isPickedUp = false;
        this.recipe = "";
        this.userName = "";
    }

    /**
     * Gets the id
     *
     * @return id of the order
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the id
     *
     * @param id
     *            new id of the order
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the user
     *
     * @return the user for the order
     */
    public Long getUser () {
        return user;
    }

    /**
     * Gets the completion status of the order
     *
     * @return true if order is complete, false if not
     */
    public boolean isComplete () {
        return isComplete;
    }

    /**
     * Gets the picked up status of the order
     *
     * @return true if order is picked up, false if not
     */
    public boolean isPickedUp () {
        return isPickedUp;
    }

    /**
     * Gets the recipe tied to the order
     *
     * @return the name of recipe for the order
     */
    public String getRecipe () {
        return recipe;
    }

    /**
     * Gets the username of the user associated with the order
     *
     * @return the username of the user who made the order
     */
    public String getUserName () {
        return userName;
    }

}
