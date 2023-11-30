package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * The IngredientUserDTO provides logic to communicate the ingredient object
 * with the user object for authentication
 */
public class IngredientUserDTO {
    /**
     * The ingredient to be added
     */
    public Ingredient ingredient;
    /**
     * The current user
     */
    public User       authUser;

    /**
     * Will return the DTO for a user interacting with an ingredient
     *
     * @param ingr
     *            the current ingredient
     * @param user
     *            the current user
     */
    public IngredientUserDTO ( final Ingredient ingr, final User user ) {
        this.ingredient = ingr;
        this.authUser = user;
    }

    /**
     * Will return the DTO for a user interacting with an ingredient
     */
    public IngredientUserDTO () {
        super();
    }

    /**
     * Gets the ingredient
     *
     * @return the ingredient
     */
    public Ingredient getIngredient () {
        return ingredient;
    }

    /**
     * Sets the ingredient
     *
     * @param ingredient
     *            the ingredient to set
     */
    public void setIngredient ( final Ingredient ingredient ) {
        this.ingredient = ingredient;
    }

    /**
     * Gets the authentication User
     *
     * @return the authUser
     */
    public User getAuthUser () {
        return authUser;
    }

    /**
     * Sets the authentication User
     *
     * @param authUser
     *            the authUser to set
     */
    public void setAuthUser ( final User authUser ) {
        this.authUser = authUser;
    }
}
