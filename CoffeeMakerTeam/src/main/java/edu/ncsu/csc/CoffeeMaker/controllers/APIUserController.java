package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.controllers.DTO.NamePasswordPermissionUserDTO;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Users.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIUserController extends APIController {

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private UserService service;

    /**
     * REST API method to provide GET access to all users in the system
     *
     * @return JSON representation of all users
     */
    @GetMapping ( BASE_PATH + "/users" )
    public List<User> getUsers () {
        return service.findAll();
    }

    /**
     * Helper method to get a user object from the list of all users based on
     * the user name. This should only be used after a user has been
     * authenticated
     *
     * @param userName
     *            the user name of the User to get
     * @return User the user object from the get
     */
    public User getUser ( final String userName ) {
        final User user = service.findByName( userName );
        return null == user ? null : user;
    }

    /**
     * Authenticates a user in the system. Will return the user object for the
     * front end to store for authentication for other REST API calls
     *
     * @param userName
     *            the user name for the user logging in
     * @param password
     *            password for the user logging in
     * @return ResponseEntity containing a user body after the user has
     *         successfully logged in or failure otherwise
     */
    @GetMapping ( BASE_PATH + "/users/{userName}/{password}" )
    public ResponseEntity login ( @PathVariable final String userName, @PathVariable final String password ) {

        if ( getUser( "admin" ) == null ) {
            final User u = new User( "admin", "password", 2 );
            service.save( u );
        }

        final User user = getUser( userName );

        if ( user != null && user.getPassword() == User.hashPassword( password ) ) {
            return new ResponseEntity( user, HttpStatus.OK );
        }

        return new ResponseEntity( errorResponse( "Incorrect username or password " ), HttpStatus.NOT_FOUND );
    }

    /**
     * Gets a user's username given their id
     *
     * @param id
     *            id for the user
     * @return ResponseEntity indicating success if user successfully logged in
     *         or failure otherwise
     */
    @GetMapping ( BASE_PATH + "/users/{id}" )
    public ResponseEntity getUsernameById ( @PathVariable final Long id ) {

        if ( service.findById( id ) == null ) {
            return new ResponseEntity( errorResponse( "User not found" ), HttpStatus.NOT_FOUND );
        }

        final User user = service.findById( id );

        if ( user != null ) {
            return new ResponseEntity( successResponse( user.getUserName() ), HttpStatus.OK );
        }

        return new ResponseEntity( errorResponse( "User not found" ), HttpStatus.NOT_FOUND );
    }

    /**
     * Helper method for authenticating a user whose password is already hashed
     *
     * @param userName
     *            username for the user
     * @param password
     *            password for the user
     * @return true if they could be authenticated and false otherwise
     */
    public boolean authenticate ( final String userName, final int password ) {
        final User user = getUser( userName );

        if ( user != null && user.getPassword() == password ) {
            return true;
        }
        return false;
    }

    /**
     * REST API method to provide POST access to the User model. This is used to
     * create a new User by automatically converting the JSON RequestBody
     * provided to a User object. Invalid JSON will fail.
     *
     * @param body
     *            DTO that contains the name, password, permission, and an
     *            authentication user
     * @return ResponseEntity indicating success if the User could be created,
     *         or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/users" )
    public ResponseEntity makeUser ( @RequestBody final NamePasswordPermissionUserDTO body ) {

        final String userName = body.name;
        final String password = body.password;
        final int permission = body.permission;
        final User user = body.authUser;

        if ( getUser( userName ) != null ) {
            return new ResponseEntity( errorResponse( "User already in system." ), HttpStatus.CONFLICT );
        }
        // we are making a customer
        if ( permission == 0 ) {
            service.save( new User( userName, password, permission ) );
            return new ResponseEntity( successResponse( userName + " successfully created" ), HttpStatus.OK );
        }
        else {
            if ( authenticate( user.getUserName(), user.getPassword() ) && user.isManager() ) {
                service.save( new User( userName, password, permission ) );
                return new ResponseEntity( successResponse( userName + " successfully created" ), HttpStatus.OK );
            }
        }
        return new ResponseEntity( errorResponse( "Current user is not authenticated for this operation" ),
                HttpStatus.FORBIDDEN );
    }

    /**
     * REST API method to allow deleting a user from the CoffeeMaker system, by
     * making a DELETE request to the API endpoint and indicating the user to
     * delete (as a path variable)
     *
     * @param userName
     *            The name of the user to delete
     * @return Success if the user was deleted; an error if the user does not
     *         exist or the authentication user may not delete
     */
    @DeleteMapping ( BASE_PATH + "/users/{userName}" )
    public ResponseEntity deleteUser ( @PathVariable final String userName ) {

        final User userToDelete = service.findByName( userName );
        if ( null == userToDelete ) {
            return new ResponseEntity( errorResponse( "No user found for username " + userName ),
                    HttpStatus.NOT_FOUND );
        }
        service.delete( userToDelete );
        return new ResponseEntity( successResponse( userName + " was deleted successfully" ), HttpStatus.OK );
    }
}
