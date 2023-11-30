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

    /**
     * Will return the DTO for a user that has been authenticated
     */
    public NamePasswordPermissionUserDTO () {
        super();
        this.permission = 0;
        this.name = "";
        this.password = "";
        this.authUser = null;
    }

    /**
     * Gets the name
     *
     * @return the name
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the name
     *
     * @param name
     *            the name to set
     */
    public void setName ( final String name ) {
        this.name = name;
    }

    /**
     * Gets the password
     *
     * @return the password
     */
    public String getPassword () {
        return password;
    }

    /**
     * Sets the password
     *
     * @param password
     *            the password to set
     */
    public void setPassword ( final String password ) {
        this.password = password;
    }

    /**
     * Gets the permission status
     *
     * @return the permission
     */
    public int getPermission () {
        return permission;
    }

    /**
     * Sets the permission status
     *
     * @param permission
     *            the permission to set
     */
    public void setPermission ( final int permission ) {
        this.permission = permission;
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
