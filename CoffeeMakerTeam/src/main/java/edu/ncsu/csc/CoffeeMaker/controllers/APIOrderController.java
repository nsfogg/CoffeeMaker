package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.controllers.DTO.PaidUserDTO;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.OrderService;
import edu.ncsu.csc.CoffeeMaker.services.RecipeService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 *
 * The APICoffeeController is responsible for making coffee when a user submits
 * a request to do so.
 *
 * Spring will automatically convert all of the ResponseEntity and List results
 * to JSON
 *
 * @author Kai Presler-Marshall
 *
 */
@SuppressWarnings ( { "unchecked", "rawtypes" } )
@RestController
public class APIOrderController extends APIController {

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService  inventoryService;

    /**
     * RecipeService object, to be autowired in by Spring to allow for
     * manipulating the Recipe model
     */
    @Autowired
    private RecipeService     recipeService;

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

    @Autowired
    private OrderService      orderService;

    /**
     * REST API method to make coffee by completing a POST request with the ID
     * of the recipe as the path variable and the amount that has been paid as
     * the body of the response
     *
     * @param name
     *            recipe name
     * @param body
     *            User information
     * @return The change the customer is due if successful
     */
    @PostMapping ( BASE_PATH + "/orders/{name}" )
    public ResponseEntity order ( @PathVariable ( "name" ) final String recipeName,
            @RequestBody final PaidUserDTO body ) {

        final int amtPaid = body.paid;
        final User user = body.authUser;
        // final Recipe recipe = body.newRecipe;

        if ( !control.authenticate( user.getUserName(), user.getPassword() ) ) {
            return new ResponseEntity( errorResponse( "Current user is not authenticated for this operation" ),
                    HttpStatus.FORBIDDEN );
        }
        final User checkUser = userService.findByName( user.getUserName() );
        if ( !checkUser.isCustomer() ) {
            return new ResponseEntity( errorResponse( "Only customers can order coffee" ), HttpStatus.BAD_REQUEST );
        }
        final Recipe recipe = recipeService.findByName( recipeName );
        if ( recipe == null ) {
            return new ResponseEntity( errorResponse( "No recipe selected" ), HttpStatus.NOT_FOUND );
        }

        final int change = makeOrder( recipe, amtPaid, user );
        if ( change == amtPaid ) {
            if ( amtPaid < recipe.getPrice() ) {
                return new ResponseEntity( errorResponse( "Not enough money paid" ), HttpStatus.CONFLICT );
            }
            else {
                return new ResponseEntity( errorResponse( "Not enough inventory" ), HttpStatus.CONFLICT );
            }
        }
        return new ResponseEntity<String>( successResponse( String.valueOf( change ) ), HttpStatus.OK );

    }

    /**
     * Helper method to make coffee
     *
     * @param toPurchase
     *            recipe that we want to make
     * @param amtPaid
     *            money that the user has given the machine
     * @return change if there was enough money to make the coffee, throws
     *         exceptions if not
     */
    public int makeOrder ( final Recipe toPurchase, final int amtPaid, final User user ) {
        int change = amtPaid;
        final Inventory inventory = inventoryService.getInventory();

        if ( toPurchase == null ) {
            throw new IllegalArgumentException( "Recipe not found" );
        }
        else if ( toPurchase.getPrice() <= amtPaid ) {
            if ( inventory.useIngredients( toPurchase ) ) {
                inventoryService.save( inventory );
                change = amtPaid - toPurchase.getPrice();

                orderService.save( user.order( toPurchase ) );
                return change;
            }
            else {
                // not enough inventory
                return change;
            }
        }
        // not enough money
        return change;
    }

    /**
     * REST API method to make coffee by completing a POST request with the ID
     * of the recipe as the path variable and the amount that has been paid as
     * the body of the response
     *
     * @param name
     *            recipe name
     * @param body
     *            User information
     * @return The change the customer is due if successful
     */
    @PostMapping ( BASE_PATH + "/order/status" )
    public ResponseEntity getOrders ( @RequestBody final User user ) {

        if ( !control.authenticate( user.getUserName(), user.getPassword() ) ) {
            return new ResponseEntity( errorResponse( "Current user is not authenticated for this operation" ),
                    HttpStatus.FORBIDDEN );
        }
        final User checkUser = userService.findByName( user.getUserName() );

        if ( checkUser.isCustomer() ) {
            return new ResponseEntity( checkUser.getOrders(), HttpStatus.OK );
        }
        return new ResponseEntity( orderService.findAll(), HttpStatus.OK );

    }

}
