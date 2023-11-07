package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.User;

public class PaidUserDTO {
    public int  paid;
    public User authUser;

    public PaidUserDTO ( final int paid, final User user ) {
        this.paid = paid;
        this.authUser = user;
    }

}
