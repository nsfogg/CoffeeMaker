package edu.ncsu.csc.CoffeeMaker.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Order;
import edu.ncsu.csc.CoffeeMaker.models.Recipe;
import edu.ncsu.csc.CoffeeMaker.models.User;

public class OrderTest {

    @Test
    @Transactional
    public void testOrder () {
        final Order order = new Order();

        Assertions.assertEquals( null, order.getUser() );
        Assertions.assertEquals( null, order.getRecipe() );
        Assertions.assertEquals( false, order.isComplete() );
        Assertions.assertEquals( false, order.isPickedUp() );

    }

    @Test
    @Transactional
    public void testOrderParams () {
        final Recipe r = new Recipe();
        r.setName( "test" );
        r.setPrice( 10 );

        final User user = new User();
        final Order order = new Order( user, r );

        Assertions.assertEquals( user, order.getUser() );
        Assertions.assertEquals( r, order.getRecipe() );
        Assertions.assertEquals( false, order.isComplete() );
        Assertions.assertEquals( false, order.isPickedUp() );
    }

    @Test
    @Transactional
    public void testCompletePickup () {
        final Recipe r = new Recipe();
        r.setName( "test" );
        r.setPrice( 10 );

        final User user = new User();
        final Order order = new Order( user, r );

        order.completeOrder();
        Assertions.assertEquals( true, order.isComplete() );
        Assertions.assertEquals( false, order.isPickedUp() );

        order.pickUpOrder();
        Assertions.assertEquals( true, order.isPickedUp() );
        Assertions.assertEquals( true, order.isComplete() );

    }

}
