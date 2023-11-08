package edu.ncsu.csc.CoffeeMaker.controllers.DTO;

import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * The NamePasswordPermissionUserDTO provides logic to communicate the user
 * object with its authentication
 */
public class NamePasswordPermissionUserDTO {
    /**
     * The users name
     */
    public String name;
    /**
     * The users password
     */
    public String password;
    /**
     * The users permission level
     */
    public int    permission;
    /**
     * Created authorized user
     */
    public User   authUser;

    /**
     * Will return the DTO for a user that has been authenticated
     *
     * @param name
     *            the users name
     * @param password
     *            the users password
     * @param permission
     *            the users permission level
     * @param user
     *            the authenticated user
     */
    public NamePasswordPermissionUserDTO ( final String name, final String password, final int permission,
            final User user ) {
        this.name = name;
        this.password = password;
        this.permission = permission;
        this.authUser = user;
    }
}
