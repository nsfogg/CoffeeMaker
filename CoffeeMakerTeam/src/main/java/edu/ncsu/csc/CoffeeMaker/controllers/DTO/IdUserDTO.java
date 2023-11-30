package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * The IdUserDTO provides logic to communicate the user object with the amount
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

    /**
     * Will return the DTO for a user and an id
     */
    public IdUserDTO () {
        super();
    }

    /**
     * Gets the id
     *
     * @return the id
     */
    public Long getId () {
        return id;
    }

    /**
     * Sets the id
     *
     * @param id
     *            the id to set
     */
    public void setId ( final Long id ) {
        this.id = id;
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
