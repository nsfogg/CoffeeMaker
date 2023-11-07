package edu.ncsu.csc.CoffeeMaker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import edu.ncsu.csc.CoffeeMaker.controllers.DTO.InventoryUserDTO;
import edu.ncsu.csc.CoffeeMaker.models.Inventory;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.InventoryService;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

/**
 * This is the controller that holds the REST endpoints that handle add and
 * update operations for the Inventory.
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
public class APIInventoryController extends APIController {

    /**
     * InventoryService object, to be autowired in by Spring to allow for
     * manipulating the Inventory model
     */
    @Autowired
    private InventoryService  service;

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
     * REST API endpoint to provide GET access to the CoffeeMaker's singleton
     * Inventory. This will convert the Inventory to JSON.
     *
     * @return response to the request
     */
    @GetMapping ( BASE_PATH + "/inventory" )
    public ResponseEntity getInventory ( @RequestBody final User user ) {
        if ( !control.authenticate( user.getUserName(), user.getPassword() ) ) {
            return new ResponseEntity( errorResponse( " Current user is not authenticated for this operation" ),
                    HttpStatus.FORBIDDEN );
        }
        final User checkUser = userService.findByName( user.getUserName() );

        if ( !checkUser.isManager() ) {
            return new ResponseEntity( errorResponse( "Cannot view the inventory" ), HttpStatus.BAD_REQUEST );
        }
        final Inventory inventory = service.getInventory();
        return new ResponseEntity( inventory, HttpStatus.OK );

    }

    /**
     * REST API endpoint to provide update access to CoffeeMaker's singleton
     * Inventory. This will update the Inventory of the CoffeeMaker by adding
     * amounts from the Inventory provided to the CoffeeMaker's stored inventory
     *
     * @param inventory
     *            amounts to add to inventory
     * @return response to the request
     */
    @PutMapping ( BASE_PATH + "/inventory" )
    public ResponseEntity updateInventory ( @RequestBody final InventoryUserDTO body ) {

        final Inventory inventory = body.inventory;
        final User user = body.authUser;

        if ( !control.authenticate( user.getUserName(), user.getPassword() ) ) {
            return new ResponseEntity( errorResponse( " Current user is not authenticated for this operation" ),
                    HttpStatus.FORBIDDEN );
        }
        final User checkUser = userService.findByName( user.getUserName() );
        if ( !checkUser.isManager() ) {

            return new ResponseEntity( errorResponse( "Cannot edit the inventory" ), HttpStatus.BAD_REQUEST );
        }
        final Inventory inventoryCurrent = service.getInventory();

        // Update the inventory
        try {
            inventoryCurrent.updateInventory( inventory.getInventory() );
        }
        catch ( final IllegalArgumentException e ) {
            // Catch and report the exception in an error response.
            return new ResponseEntity( errorResponse( e.getMessage() ), HttpStatus.BAD_REQUEST );
        }

        // Save it.
        service.save( inventoryCurrent );
        return new ResponseEntity( inventoryCurrent, HttpStatus.OK );
    }

}
