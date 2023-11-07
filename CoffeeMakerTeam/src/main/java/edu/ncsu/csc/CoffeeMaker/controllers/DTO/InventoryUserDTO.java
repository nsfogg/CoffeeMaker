package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.User;

public class InventoryUserDTO {
    public Inventory inventory;
    public User      authUser;

    public InventoryUserDTO ( final Inventory inv, final User user ) {
        this.inventory = inv;
        this.authUser = user;
    }
}
