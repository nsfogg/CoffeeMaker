package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * The PaidUserDTO provides logic to communicate the user object with the amount
 * paid
 */
public class IdUserDTO {
    /**
     * The paid amount
     */
    public Long id;
    /**
     * The current user
     */
    public User authUser;

    /**
     * Will return the DTO for a user and an id
     *
     * @param id
     *            the id
     * @param user
     *            the current user
     */
    public IdUserDTO ( final Long id, final User user ) {
        this.id = id;
        this.authUser = user;
    }

}
