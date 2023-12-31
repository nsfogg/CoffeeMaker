package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.controllers.DTO.RecipeUserDTO;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
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
public class APIRecipeController extends APIController {

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model
     */
    @Autowired
    private RecipeService     service;

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private UserService       userService;

    /**
     * UserController object, to be autowired in by Spring to allow for
     * manipulating the User Controller
     */
    @Autowired
    private APIUserController control;

    /**
     * REST API method to provide GET access to all recipes in the system
     *
     * @param userName
     *            the authentication user name
     *
     * @param password
     *            the hashed password for the authentication user
     *
     * @return JSON representation of all recipes
     */
    @GetMapping ( BASE_PATH + "/recipes/" )
    public List<Recipe> getRecipes ( @RequestParam ( name = "userName", required = true ) final String userName,
            @RequestParam ( name = "password", required = true ) final Integer password ) {
        if ( !control.authenticate( userName, password ) ) {
            return null;
        }

        // Return all recipes since everyone should have access to view the
        // recipes
        return service.findAll();
    }

    /**
     * REST API method to provide GET access to a specific recipe, as indicated
     * by the path variable provided (the name of the recipe desired)
     *
     * @param name
     *            recipe name
     * @param userName
     *            the authentication user name
     * @param password
     *            the hashed password of the authentication user
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/recipes/{name}/" )
    public ResponseEntity getRecipe ( @PathVariable ( "name" ) final String name,
            @RequestParam ( name = "userName", required = true ) final String userName,
            @RequestParam ( name = "password", required = true ) final Integer password ) {
        if ( !control.authenticate( userName, password ) ) {
            return new ResponseEntity( errorResponse( " Current user is not authenticated for this operation" ),
                    HttpStatus.FORBIDDEN );
        }
        final Recipe recipe = service.findByName( name );
        // final User checkUser = userService.findByName( user.getUserName() );
        // if ( checkUser.isCustomer() ) {
        // return new ResponseEntity( errorResponse( " Cannot view" ),
        // HttpStatus.FORBIDDEN );
        // }
        return null == recipe
                ? new ResponseEntity( errorResponse( "No recipe found with name " + name ), HttpStatus.NOT_FOUND )
                : new ResponseEntity( recipe, HttpStatus.OK );
    }

    /**
     * REST API method to provide POST access to the Recipe model. This is used
     * to create a new Recipe by automatically converting the JSON RequestBody
     * provided to a Recipe object. Invalid JSON will fail.
     *
     * @param body
     *            current users information to be authenticated
     * @return ResponseEntity indicating success if the Recipe could be saved to
     *         the inventory, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/recipes" )
    public ResponseEntity createRecipe ( @RequestBody final RecipeUserDTO body ) {

        final User user = body.authUser;
        final Recipe recipe = body.newRecipe;

        if ( !control.authenticate( user.getUserName(), user.getPassword() ) ) {
            return new ResponseEntity( errorResponse( " Current user is not authenticated for this operation" ),
                    HttpStatus.FORBIDDEN );
        }
        final User checkUser = userService.findByName( user.getUserName() );
        if ( !checkUser.isManager() ) {
            return new ResponseEntity( errorResponse( " Current user cannot create a Recipe" ), HttpStatus.FORBIDDEN );
        }
        if ( null != service.findByName( recipe.getName() ) ) {
            return new ResponseEntity( errorResponse( "Recipe with the name " + recipe.getName() + " already exists" ),
                    HttpStatus.CONFLICT );
        }
        if ( recipe.getIngredients().size() == 0 ) {
            return new ResponseEntity( errorResponse( recipe.getName() + " cannot be created without ingredients" ),
                    HttpStatus.FORBIDDEN );
        }
        for ( final Entry<Ingredient, Integer> i : recipe.getIngredients().entrySet() ) {
            if ( i.getValue() <= 0 ) {
                return new ResponseEntity(
                        errorResponse( recipe.getName() + " cannot be have an ingredient with an amount of 0" ),
                        HttpStatus.FORBIDDEN );
            }
        }
        // if ( recipe.getIngredients().containsValue( 0 ) ) {
        // return new ResponseEntity(
        // errorResponse( recipe.getName() + " cannot be have an ingredient with
        // an amount of 0" ),
        // HttpStatus.FORBIDDEN );
        // }
        if ( service.findAll().size() < 3 ) {
            service.save( recipe );
            return new ResponseEntity( successResponse( recipe.getName() + " successfully created" ), HttpStatus.OK );
        }
        else {
            return new ResponseEntity(
                    errorResponse( "Insufficient space in recipe book for recipe " + recipe.getName() ),
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
     * @param userName
     *            the username
     * @param password
     *            the password
     * @return Success if the recipe could be deleted; an error if the recipe
     *         does not exist
     */
    @DeleteMapping ( BASE_PATH + "/recipes/" )
    public ResponseEntity deleteRecipe ( @RequestParam ( name = "name", required = true ) final String name,
            @RequestParam ( name = "userName", required = true ) final String userName,
            @RequestParam ( name = "password", required = true ) final Integer password ) {
        if ( !control.authenticate( userName, password ) ) {
            return new ResponseEntity( errorResponse( " Current user is not authenticated for this operation" ),
                    HttpStatus.FORBIDDEN );
        }
        final User checkUser = userService.findByName( userName );
        if ( !checkUser.isManager() ) {
            return new ResponseEntity( errorResponse( " Current user cannot create a Recipe" ), HttpStatus.FORBIDDEN );
        }
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
     * @param body
     *            current users information to authenticate
     * @return Success if the recipe could be updated; an error if the recipe
     *         does not exist
     */
    @PutMapping ( BASE_PATH + "/recipes/{name}" )
    public ResponseEntity editRecipe ( @PathVariable final String name, @RequestBody final RecipeUserDTO body ) {

        final User user = body.authUser;
        final Recipe recipe = body.newRecipe;

        if ( !control.authenticate( user.getUserName(), user.getPassword() ) ) {
            return new ResponseEntity( errorResponse( " Current user is not authenticated for this operation" ),
                    HttpStatus.FORBIDDEN );
        }
        final User checkUser = userService.findByName( user.getUserName() );
        if ( !checkUser.isManager() ) {
            return new ResponseEntity( errorResponse( " Current user cannot create a Recipe" ), HttpStatus.FORBIDDEN );
        }
        final Recipe r = service.findByName( name );
        if ( null == r ) {
            return new ResponseEntity( errorResponse( "No recipe found for name " + name ), HttpStatus.NOT_FOUND );
        }
        if ( recipe.getIngredients().size() == 0 ) {
            return new ResponseEntity( errorResponse( recipe.getName() + " cannot be edited to have no ingredients" ),
                    HttpStatus.FORBIDDEN );
        }
        if ( recipe.getIngredients().containsValue( 0 ) ) {
            return new ResponseEntity(
                    errorResponse( recipe.getName() + " cannot be have an ingredient with an amount of 0" ),
                    HttpStatus.FORBIDDEN );
        }

        r.updateRecipe( recipe );
        service.save( r );
        return new ResponseEntity( successResponse( name + " was successfully updated." ), HttpStatus.OK );
    }

}
