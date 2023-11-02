package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * This is the controller that holds the REST endpoints that handle CRUD
 * operations for Recipes.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kai Presler-Marshall
 * @author Michelle Lemons
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIUserController extends APIController {

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model
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
     * REST API method to provide GET access to a specific user, as indicated by
     * the path variable provided (the name of the user desired)
     *
     * @param name
     *            user name
     * @return User
     */
    public User getUser ( final String userName ) {
        final User user = service.findByName( userName );
        return null == user ? null : user;
    }

    @GetMapping ( BASE_PATH + "/users/{userName}" )
    public ResponseEntity login ( @PathVariable final String userName, @RequestBody final String password ) {
        final User user = getUser( userName );

        if ( user != null && user.getPassword() == password.hashCode() ) {
            return new ResponseEntity( user, HttpStatus.OK );
        }

        return new ResponseEntity( errorResponse( "Incorrect username or password " ), HttpStatus.NOT_FOUND );
    }

    public boolean authenticate ( final String userName, final int password ) {
        final User user = getUser( userName );

        if ( user != null && user.getPassword() == password ) {
            return true;
        }

        return false;
    }

    /**
     * REST API method to provide POST access to the Recipe model. This is used
     * to create a new Recipe by automatically converting the JSON RequestBody
     * provided to a Recipe object. Invalid JSON will fail.
     *
     * @param recipe
     *            The valid Recipe to be saved.
     * @return ResponseEntity indicating success if the Recipe could be saved to
     *         the inventory, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/users" )
    public ResponseEntity makeUser ( @RequestBody final User user ) {
        if ( !authenticate( user.getUserName(), user.getPassword() ) ) {
            return new ResponseEntity( errorResponse( "User cannot be authenticated " ), HttpStatus.NOT_FOUND );
        }

        if ( null != service.findByName( user.getUserName() ) ) {
            return new ResponseEntity( errorResponse( "User with the name " + user.getUserName() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        if ( service.findAll().size() < 3 ) {
            service.save( user );
            return new ResponseEntity( successResponse( user.getUserName() + " successfully created" ), HttpStatus.OK );
        }
        else {
            return new ResponseEntity(
                    errorResponse( "Insufficient space in recipe book for recipe " + user.getUserName() ),
                    HttpStatus.INSUFFICIENT_STORAGE );
        }

    }

    /**
     * REST API method to allow deleting a Recipe from the CoffeeMaker's
     * Inventory, by making a DELETE request to the API endpoint and indicating
     * the recipe to delete (as a path variable)
     *
     * @param name
     *            The name of the Recipe to delete
     * @return Success if the recipe could be deleted; an error if the recipe
     *         does not exist
     */
    @DeleteMapping ( BASE_PATH + "/recipes/{name}" )
    public ResponseEntity deleteRecipe ( @PathVariable final String name ) {
        final Recipe recipe = service.findByName( name );
        if ( null == recipe ) {
            return new ResponseEntity( errorResponse( "No recipe found for name " + name ), HttpStatus.NOT_FOUND );
        }
        service.delete( recipe );

        return new ResponseEntity( successResponse( name + " was deleted successfully" ), HttpStatus.OK );
    }

    /**
     * REST API method to allow updating a Recipe from the CoffeeMaker's
     * Inventory, by making a PUT request to the API endpoint and indicating the
     * recipe to update (as a path variable)
     *
     * @param name
     *            name of the recipe to update
     * @param recipe
     *            recipe to be updated
     * @return Success if the recipe could be updated; an error if the recipe
     *         does not exist
     */
    @PutMapping ( BASE_PATH + "/recipes/{name}" )
    public ResponseEntity editRecipe ( @PathVariable final String name, @RequestBody final Recipe recipe ) {

        final Recipe r = service.findByName( name );
        if ( null == r ) {
            return new ResponseEntity( errorResponse( "No recipe found for name " + name ), HttpStatus.NOT_FOUND );
        }

        r.updateRecipe( recipe );
        service.save( r );
        return new ResponseEntity( successResponse( name + " was successfully updated." ), HttpStatus.OK );
    }
}
