package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;

public class RecipeUserDTO {
    public Recipe newRecipe;
    public User   authUser;

    public RecipeUserDTO ( final Recipe r, final User u ) {
        this.newRecipe = r;
        this.authUser = u;
    }
}
