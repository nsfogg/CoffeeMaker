package edu.ncsu.csc.CoffeeMaker.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;

/**
 * Will test the Order class, methods, and expected exceptions
 */
public class OrderTest {

    /**
     * Will test creating an order object
     */
    @Test
    @Transactional
    public void testOrder () {
        final Order order = new Order();

        Assertions.assertEquals( 0, order.getUser() );
        Assertions.assertEquals( null, order.getRecipe() );
        Assertions.assertEquals( false, order.isComplete() );
        Assertions.assertEquals( false, order.isPickedUp() );

    }

    /**
     * Will test creating an order object wiht the correct parameters
     */
    @Test
    @Transactional
    public void testOrderParams () {
        final Recipe r = new Recipe();
        r.setName( "test" );
        r.setPrice( 10 );

        final User user = new User();
        final Order order = new Order( user.getId(), r );

        Assertions.assertEquals( user.getId(), order.getUser() );
        Assertions.assertEquals( r.getName(), order.getRecipe() );
        Assertions.assertEquals( false, order.isComplete() );
        Assertions.assertEquals( false, order.isPickedUp() );
    }

    /**
     * Will test fufilling an Order
     */
    @Test
    @Transactional
    public void testCompletePickup () {
        final Recipe r = new Recipe();
        r.setName( "test" );
        r.setPrice( 10 );

        final User user = new User();
        final Order order = new Order( user.getId(), r );

        order.completeOrder();
        Assertions.assertEquals( true, order.isComplete() );
        Assertions.assertEquals( false, order.isPickedUp() );

        order.pickUpOrder();
        Assertions.assertEquals( true, order.isPickedUp() );
        Assertions.assertEquals( true, order.isComplete() );

    }

}
