package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.User;

public class NamePasswordPermissionUserDTO {
    public String name;
    public String password;
    public int    permission;
    public User   authUser;

    public NamePasswordPermissionUserDTO ( final String name, final String password, final int permission,
            final User user ) {
        this.name = name;
        this.password = password;
        this.permission = permission;
        this.authUser = user;
    }
}
