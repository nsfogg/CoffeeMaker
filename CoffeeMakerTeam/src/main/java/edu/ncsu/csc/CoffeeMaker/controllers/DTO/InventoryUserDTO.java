package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * The InventoryUserDTO provides logic to communicate the inventory object with
 * the user object for authentication
 */
public class InventoryUserDTO {
    /**
     * The current inventory
     */
    public Inventory inventory;

    /**
     * The current user
     */
    public User      authUser;

    /**
     * Will return the DTO for a user interacting with the inventory
     *
     * @param inv
     *            the current inventory
     * @param user
     *            the current user
     */
    public InventoryUserDTO ( final Inventory inv, final User user ) {
        this.inventory = inv;
        this.authUser = user;
    }
}
