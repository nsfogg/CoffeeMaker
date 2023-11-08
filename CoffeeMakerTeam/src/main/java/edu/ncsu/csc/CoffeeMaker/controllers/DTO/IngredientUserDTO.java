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
}
