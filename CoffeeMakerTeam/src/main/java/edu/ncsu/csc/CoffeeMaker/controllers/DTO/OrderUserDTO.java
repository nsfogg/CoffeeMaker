package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.Order;

public class OrderUserDTO {

    public Long          id = 0L;

    public final Long    user;

    public final boolean isComplete;

    public final boolean isPickedUp;

    public final String  recipe;

    public final String  userName;

    public OrderUserDTO ( final Order order, final String userName ) {

        this.id = order.getId();
        this.user = order.getUser();
        this.isComplete = order.isComplete();
        this.isPickedUp = order.isPickedUp();
        this.recipe = order.getRecipe();
        this.userName = userName;
    }

    public OrderUserDTO () {
        this.id = 0L;
        this.user = 0L;
        this.isComplete = false;
        this.isPickedUp = false;
        this.recipe = "";
        this.userName = "";
    }

    public Long getId () {
        return id;
    }

    public void setId ( final Long id ) {
        this.id = id;
    }

    public Long getUser () {
        return user;
    }

    public boolean isComplete () {
        return isComplete;
    }

    public boolean isPickedUp () {
        return isPickedUp;
    }

    public String getRecipe () {
        return recipe;
    }

    public String getUserName () {
        return userName;
    }

}
