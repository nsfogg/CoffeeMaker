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

    /**
     * Will return the DTO for a user that has requested the information of a
     * recipe
     */
    public RecipeUserDTO () {
        super();
    }

    /**
     * Gets the new recipe object
     *
     * @return the newRecipe
     */
    public Recipe getNewRecipe () {
        return newRecipe;
    }

    /**
     * Sets the new recipe object
     *
     * @param newRecipe
     *            the newRecipe to set
     */
    public void setNewRecipe ( final Recipe newRecipe ) {
        this.newRecipe = newRecipe;
    }

    /**
     * Gets the authentication user
     *
     * @return the authUser
     */
    public User getAuthUser () {
        return authUser;
    }

    /**
     * Sets the authentication user
     *
     * @param authUser
     *            the authUser to set
     */
    public void setAuthUser ( final User authUser ) {
        this.authUser = authUser;
    }
}
