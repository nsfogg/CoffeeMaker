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

    /**
     *
     */
    public InventoryUserDTO () {
        super();
    }

    /**
     * @return the inventory
     */
    public Inventory getInventory () {
        return inventory;
    }

    /**
     * @param inventory
     *            the inventory to set
     */
    public void setInventory ( final Inventory inventory ) {
        this.inventory = inventory;
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
