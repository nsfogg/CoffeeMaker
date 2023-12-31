package edu.ncsu.csc.CoffeeMaker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.controllers.DTO.IngredientUserDTO;
import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.IngredientService;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * Controller class for Ingredient API, works with IngredientService and Recipe
 * Service and Inventory Service to implement all API methods for the Ingredient
 * functionality. This class extends APIController
 */
@RestController
@SuppressWarnings ( { "rawtypes", "unchecked" } )
public class APIIngredientController extends APIController {

    /**
     * IngredientService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private IngredientService ingredientService;

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private RecipeService     recipeService;

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService  inventoryService;

    /**
     * APIUserController object, to be autowired in by Spring to allow for
     * manipulating the User Controller
     */
    @Autowired
    private APIUserController control;

    /**
     * UserService object, to be autowired in by Spring to allow for
     * manipulating the User model
     */
    @Autowired
    private UserService       userService;

    /**
     * REST API endpoint to provide GET access to the CoffeeMaker's ingredients
     * list. This will convert the ingredients list to a JSON Array of
     * Ingredient objects.
     *
     * @param password
     *            the hashed password for the authentication user
     * @param userName
     *            the user name for the authentication user
     * @return The list of ingredients
     */
    @GetMapping ( BASE_PATH + "/ingredients" )
    public ResponseEntity getIngredients ( @RequestParam ( name = "userName", required = true ) final String userName,
            @RequestParam ( name = "password", required = true ) final Integer password ) {
        if ( !control.authenticate( userName, password ) ) {
            return new ResponseEntity( errorResponse( " Current user is not authenticated for this operation" ),
                    HttpStatus.FORBIDDEN );
        }
        final User checkUser = userService.findByName( userName );

        if ( checkUser.isCustomer() || checkUser.isBarista() ) {
            return new ResponseEntity( errorResponse( "Cannot view the current lsit of ingredients" ),
                    HttpStatus.FORBIDDEN );
        }
        final List<Ingredient> ingredients = ingredientService.findAll();
        return new ResponseEntity( ingredients, HttpStatus.OK );
    }

    /**
     * REST API endpoint to provide GET access to an Ingredient Object by name
     * from the CoffeeMaker DB, this will convert the object to JSON.
     *
     * @param name
     *            in which to get the ingredient with
     * @param user
     *            the current user
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/ingredients/{name}" )
    public ResponseEntity getIngredient ( @PathVariable final String name, @RequestBody final User user ) {
        if ( !control.authenticate( user.getUserName(), user.getPassword() ) ) {
            return new ResponseEntity( errorResponse( " Current user is not authenticated for this operation" ),
                    HttpStatus.FORBIDDEN );
        }
        final Ingredient ingredient = ingredientService.findByName( name );
        final User currUser = userService.findByName( user.getUserName() );
        if ( currUser.isCustomer() ) {
            return new ResponseEntity( errorResponse( "Cannot view the current lsit of ingredients" ),
                    HttpStatus.FORBIDDEN );
        }
        if ( ingredient != null ) {
            return new ResponseEntity( ingredient, HttpStatus.OK );
        }
        else {
            return new ResponseEntity( errorResponse( "Ingredient with the name " + name + "was not found" ),
                    HttpStatus.NOT_FOUND );
        }
    }

    /**
     * REST API method to provide POST access to the Ingredient model. This is
     * used to create a new Ingredient by automatically converting the JSON
     * RequestBody provided to a Ingredient object. Invalid JSON will fail. If
     * the initial amount passed in the request body is invalid an error
     * response will be returned and DB changes will be rolled back.
     *
     * @param body
     *            The Current representation of the user to authenticate
     * @param amount
     *            the initial amount of the ingredient in the inventory.
     *
     * @return ResponseEntity indicating success if the Ingredient could be
     *         saved to the db, or an error if it could not be
     */
    @PostMapping ( BASE_PATH + "/ingredients" )
    public ResponseEntity createIngredient ( @RequestBody final IngredientUserDTO body,
            @RequestParam ( "amount" ) final Integer amount ) {

        final Ingredient ingredient = body.ingredient;
        final User user = body.authUser;

        if ( !control.authenticate( user.getUserName(), user.getPassword() ) ) {
            return new ResponseEntity( errorResponse( " Current user is not authenticated for this operation" ),
                    HttpStatus.FORBIDDEN );
        }
        final User currUser = userService.findByName( user.getUserName() );
        if ( currUser.isCustomer() || currUser.isBarista() ) {
            return new ResponseEntity( errorResponse( "Cannot create a new ingredient" ), HttpStatus.FORBIDDEN );
        }
        if ( null != ingredientService.findByName( ingredient.getName() ) ) {
            return new ResponseEntity(
                    errorResponse( "Ingredient with the name " + ingredient.getName() + " already exists" ),
                    HttpStatus.CONFLICT );
        }

        try {
            createIngredientImpl( ingredient, amount );
        }
        catch ( final IllegalArgumentException e ) {
            return new ResponseEntity( errorResponse( e.getMessage() ), HttpStatus.BAD_REQUEST );
        }

        return new ResponseEntity( successResponse( ingredient.getName() + " successfully created" ), HttpStatus.OK );

    }

    /**
     * Helper method that is the implementation of createIngredient() wrapped
     * with @Transctional to rollback changes on an IAexception being thrown.
     *
     * @param ingredient
     *            The valid Ingredient to be saved.
     * @param amount
     *            the initial amount of the ingredient in the inventory.
     *
     * @throws IllegalArgumentException
     *             if the initial amount for an ingredient is negative, or the
     *             ingredient to be added is already in the inventory.
     */
    @Transactional ( rollbackFor = IllegalArgumentException.class )
    private void createIngredientImpl ( final Ingredient ingredient, final Integer amount )
            throws IllegalArgumentException {

        // Save the ingredient
        ingredientService.save( ingredient );

        // Retrieve and update the inventory with the new ingredient and its
        // initial amount
        final Inventory inventory = inventoryService.getInventory();

        inventory.addNewIngredient( ingredientService.findByName( ingredient.getName() ), amount );

        // Save the inventory
        inventoryService.save( inventory );
    }

    /**
     * REST API method to provide OUT access to the Ingredient model. This is
     * used to update an Ingredient by automatically converting the JSON
     * RequestBody provided to a Ingredient object. Invalid JSON will fail. If
     * the name passed in the request body is invalid an error response will be
     * returned and DB changes will be rolled back.
     *
     * @param name
     *            the name of the ingredient to update
     * @param body
     *            the current users information to authenticate
     * @return response to the request
     */
    @PutMapping ( BASE_PATH + "/ingredients/{name}" )
    public ResponseEntity updateIngredient ( @PathVariable final String name,
            @RequestBody final IngredientUserDTO body ) {

        final Ingredient ingredient = body.ingredient;
        final User user = body.authUser;

        if ( !control.authenticate( user.getUserName(), user.getPassword() ) ) {
            return new ResponseEntity( errorResponse( " Current user is not authenticated for this operation" ),
                    HttpStatus.FORBIDDEN );
        }
        final User currUser = userService.findByName( user.getUserName() );
        if ( currUser.isCustomer() || currUser.isBarista() ) {
            return new ResponseEntity( errorResponse( "Cannot update a ingredient based on current permissions" ),
                    HttpStatus.FORBIDDEN );
        }
        final Ingredient i = ingredientService.findByName( name );
        if ( null == i ) {
            return new ResponseEntity( errorResponse( "Ingredient with the name " + name + " does not exist" ),
                    HttpStatus.NOT_FOUND );
        }

        // Update the name of the ingredient
        i.setName( ingredient.getName() );

        ingredientService.save( i );
        return new ResponseEntity( successResponse( ingredient.getName() + " successfully created" ), HttpStatus.OK );
    }

    /**
     * REST API method to allow deleting an Ingredient from the CoffeeMaker's
     * DB, by making a DELETE request to the API endpoint and indicating the
     * ingredient to delete (as a path variable). The ingredient will be deleted
     * from any recipe that contains it. The ingredient will also be deleted
     * from the inventory.
     *
     * @param name
     *            The name of the Ingredient to delete
     * @param user
     *            the current user
     * @return Success if the ingredient could be deleted; an error if the
     *         ingredient does not exist
     */
    @DeleteMapping ( BASE_PATH + "/ingredients/{name}" )
    public ResponseEntity deleteIngredient ( @PathVariable final String name, @RequestBody final User user ) {
        if ( !control.authenticate( user.getUserName(), user.getPassword() ) ) {
            return new ResponseEntity( errorResponse( " Current user is not authenticated for this operation" ),
                    HttpStatus.FORBIDDEN );
        }
        final User currUser = userService.findByName( user.getUserName() );
        if ( currUser.isCustomer() || currUser.isBarista() ) {
            return new ResponseEntity( errorResponse( "Cannot update a ingredient based on current permissions" ),
                    HttpStatus.FORBIDDEN );
        }
        final Ingredient ingredient = ingredientService.findByName( name );
        if ( null == ingredient ) {
            return new ResponseEntity( errorResponse( "No ingredient found for name " + name ), HttpStatus.NOT_FOUND );
        }

        // Remove any recipe that contains this ingredient
        final List<Recipe> recipes = recipeService.findAll();
        for ( final Recipe r : recipes ) {

            final Integer result = r.getIngredients().remove( ingredient );

            // If the recipe had that ingredient then the return result will not
            // be null and the new recipe should be saved.
            if ( result != null ) {
                recipeService.save( r );
            }
        }
        // Remove the ingredient from the inventory
        final Inventory inventory = inventoryService.getInventory();
        inventory.getInventory().remove( ingredient );
        inventoryService.save( inventory );

        // Now we can finally delete the ingredient from the DB without causing
        // a chain reaction.
        // The reason we have to clean up after ourselves is because Recipe and
        // Inventory both use
        // the Ingredients Id as a foreign key. Operations can not cascade up to
        // the parent, only
        // parent to child.
        ingredientService.delete( ingredient );

        return new ResponseEntity( successResponse( name + " was deleted successfully" ), HttpStatus.OK );
    }
}
