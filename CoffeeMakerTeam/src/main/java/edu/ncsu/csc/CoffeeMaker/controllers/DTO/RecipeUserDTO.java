package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * The RecipeUserDTO provides logic to communicate the user object with a recipe
 */
public class RecipeUserDTO {
    /**
     * The recipe to check
     */
    public Recipe newRecipe;
    /**
     * Current user
     */
    public User   authUser;

    /**
     * Will return the DTO for a user that has requested the information of a
     * recipe
     *
     * @param r
     *            the recipe
     * @param u
     *            the current user
     */
    public RecipeUserDTO ( final Recipe r, final User u ) {
        this.newRecipe = r;
        this.authUser = u;
    }
}
