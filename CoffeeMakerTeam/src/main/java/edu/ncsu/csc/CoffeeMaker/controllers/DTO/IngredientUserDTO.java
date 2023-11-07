package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.User;

public class IngredientUserDTO {
    public Ingredient ingredient;
    public User       authUser;

    public IngredientUserDTO ( final Ingredient ingr, final User user ) {
        this.ingredient = ingr;
        this.authUser = user;
    }
}
