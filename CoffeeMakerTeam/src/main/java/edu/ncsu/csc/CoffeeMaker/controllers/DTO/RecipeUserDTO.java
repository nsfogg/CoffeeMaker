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
     *
     */
    public RecipeUserDTO () {
        super();
    }

    /**
     * @return the newRecipe
     */
    public Recipe getNewRecipe () {
        return newRecipe;
    }

    /**
     * @param newRecipe
     *            the newRecipe to set
     */
    public void setNewRecipe ( final Recipe newRecipe ) {
        this.newRecipe = newRecipe;
    }

    /**
     * @return the authUser
     */
    public User getAuthUser () {
        return authUser;
    }

    /**
     * @param authUser
     *            the authUser to set
     */
    public void setAuthUser ( final User authUser ) {
        this.authUser = authUser;
    }
}
