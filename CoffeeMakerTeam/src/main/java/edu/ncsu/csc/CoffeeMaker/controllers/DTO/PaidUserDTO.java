package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * The PaidUserDTO provides logic to communicate the user object with the amount
 * paid
 */
public class PaidUserDTO {
    /**
     * The paid amount
     */
    public int  paid;
    /**
     * The current user
     */
    public User authUser;

    /**
     * Will return the DTO for a user that has paid for their order
     *
     * @param paid
     *            the amount paid
     * @param user
     *            the current user
     */
    public PaidUserDTO ( final int paid, final User user ) {
        this.paid = paid;
        this.authUser = user;
    }

    /**
     *
     */
    public PaidUserDTO () {
        super();
    }

    /**
     * @return the paid
     */
    public int getPaid () {
        return paid;
    }

    /**
     * @param paid
     *            the paid to set
     */
    public void setPaid ( final int paid ) {
        this.paid = paid;
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
