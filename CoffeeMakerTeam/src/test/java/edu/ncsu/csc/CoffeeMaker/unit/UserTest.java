package edu.ncsu.csc.CoffeeMaker.unit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.TestConfig;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@ExtendWith ( SpringExtension.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class UserTest {
    // @Autowired
    // private RecipeService service;

    // @Autowired
    // private IngredientService ingredientService;
    //
    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup () {
        userService.deleteAll();
    }

    @Test
    @Transactional
    public void testCreateUserCustomer () {
        Assertions.assertEquals( 0, userService.findAll().size(), "There are currently no users in the system" );

        final String pw = "passwordC";
        final User customer = new User( "customer1", pw, 0 );
        Assertions.assertEquals( "customer1", customer.getUserName() );
        final int hashedPWC = pw.hashCode();
        Assertions.assertEquals( hashedPWC, customer.getPassword() );
        Assertions.assertTrue( customer.isCustomer() );
        Assertions.assertEquals( 0, customer.getPermissions() );
        userService.save( customer );
        Assertions.assertEquals( 1, userService.count() );

        // include order after implemented
    }

    @Test
    @Transactional
    public void testCreateUserBarista () {
        userService.deleteAll();
        Assertions.assertEquals( 0, userService.findAll().size(), "There are currently no users in the system" );

        final String pw = "passwordB";
        final User barista = new User( "barista1", pw, 1 );
        Assertions.assertEquals( "barista1", barista.getUserName() );
        final int hashedPWB = pw.hashCode();
        Assertions.assertEquals( hashedPWB, barista.getPassword() );
        Assertions.assertTrue( barista.isBarista() );
        Assertions.assertFalse( barista.isCustomer() );
        Assertions.assertEquals( 1, barista.getPermissions() );
        userService.save( barista );
        Assertions.assertEquals( 1, userService.count() );

        // include order after implemented
    }

    @Test
    @Transactional
    public void testCreateUserManager () {
        userService.deleteAll();
        Assertions.assertEquals( 0, userService.findAll().size(), "There are currently no users in the system" );

        final String pw = "passwordM";
        final User manager = new User( "manager1", pw, 2 );
        Assertions.assertEquals( "manager1", manager.getUserName() );
        final int hashedPWM = pw.hashCode();
        Assertions.assertEquals( hashedPWM, manager.getPassword() );
        Assertions.assertTrue( manager.isManager() );
        Assertions.assertFalse( manager.isBarista() );
        Assertions.assertEquals( 2, manager.getPermissions() );
        userService.save( manager );
        Assertions.assertEquals( 1, userService.count() );

        // include order after implemented
    }

    @Test
    @Transactional
    public void testCreateUser () {
        userService.deleteAll();
        Assertions.assertEquals( 0, userService.findAll().size(), "There are currently no users in the system" );

        final String pw = "passwordF";
        final Long id = (long) 102;
        final User manager = new User( "manager1", id, pw, 2 );
        Assertions.assertEquals( "manager1", manager.getUserName() );
        Assertions.assertEquals( id, manager.getId() );
        final int hashedPWM = pw.hashCode();
        Assertions.assertEquals( hashedPWM, manager.getPassword() );
        Assertions.assertTrue( manager.isManager() );
        Assertions.assertFalse( manager.isBarista() );
        Assertions.assertEquals( 2, manager.getPermissions() );
        userService.save( manager );
        Assertions.assertEquals( 1, userService.count() );

        // include order after implemented
    }
}
