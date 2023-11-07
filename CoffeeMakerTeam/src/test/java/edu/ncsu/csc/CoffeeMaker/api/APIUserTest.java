package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.CoffeeMaker.common.TestUtils;
import edu.ncsu.csc.CoffeeMaker.models.User;
import edu.ncsu.csc.CoffeeMaker.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith ( SpringExtension.class )
public class APIUserTest {

    /**
     * MockMvc uses Spring's testing framework to handle requests to the REST
     * API
     */
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService           service;

    User                          u = null;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
        u = new User( "admin", "password", 2 );
        service.save( u );
    }

    @Test
    @Transactional
    public void makeUser () throws Exception {

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON ).param( "userName", "user1" )
                .param( "password", "password" ).param( "permission", "0" ).content( TestUtils.asJsonString( u ) ) )
                .andExpect( status().isOk() );

        User fetchedUser = service.findByName( "user1" );
        assertEquals( 2, service.count() );
        assertEquals( "user1", fetchedUser.getUserName() );
        assertEquals( 0, fetchedUser.getPermissions().intValue() );
        assertEquals( User.hashPassword( "password" ), fetchedUser.getPassword() );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON ).param( "userName", "user1" )
                .param( "password", "password" ).param( "permission", "0" ).content( TestUtils.asJsonString( u ) ) )
                .andExpect( status().isConflict() );

        assertEquals( 2, service.count() );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON ).param( "userName", "user2" )
                .param( "password", "complexPassword" ).param( "permission", "0" )
                .content( TestUtils.asJsonString( new User() ) ) ).andExpect( status().isOk() );

        fetchedUser = service.findByName( "user2" );
        assertEquals( 3, service.count() );
        assertEquals( "user2", fetchedUser.getUserName() );
        assertEquals( 0, fetchedUser.getPermissions().intValue() );
        assertEquals( User.hashPassword( "complexPassword" ), fetchedUser.getPassword() );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON ).param( "userName", "barista" )
                .param( "password", "badPassword" ).param( "permission", "1" )
                .content( TestUtils.asJsonString( new User() ) ) ).andExpect( status().isForbidden() );

        assertEquals( 3, service.count() );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON ).param( "userName", "barista" )
                .param( "password", "badPassword" ).param( "permission", "1" ).content( TestUtils.asJsonString( u ) ) )
                .andExpect( status().isOk() );

        fetchedUser = service.findByName( "barista" );
        assertEquals( 4, service.count() );
        assertEquals( "barista", fetchedUser.getUserName() );
        assertEquals( 1, fetchedUser.getPermissions().intValue() );
        assertEquals( User.hashPassword( "badPassword" ), fetchedUser.getPassword() );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON ).param( "userName", "manager" )
                .param( "password", "iamthebest" ).param( "permission", "2" )
                .content( TestUtils.asJsonString( new User() ) ) ).andExpect( status().isForbidden() );

        assertEquals( 4, service.count() );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON ).param( "userName", "manager" )
                .param( "password", "iamthebest" ).param( "permission", "2" ).content( TestUtils.asJsonString( u ) ) )
                .andExpect( status().isOk() );

        fetchedUser = service.findByName( "manager" );
        assertEquals( 5, service.count() );
        assertEquals( "manager", fetchedUser.getUserName() );
        assertEquals( 2, fetchedUser.getPermissions().intValue() );
        assertEquals( User.hashPassword( "iamthebest" ), fetchedUser.getPassword() );

    }

    @Test
    @Transactional
    public void testUserAPI () throws Exception {

        service.deleteAll();

        final User user = new User();

        user.setUserName( "user1" );
        user.setPassword( 123456789 );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( user ) ) );

        Assertions.assertEquals( 1, (int) service.count() );

    }

    // @Test
    // @Transactional
    // public void testGetUserByNameAPI () throws Exception {
    // service.deleteAll();
    //
    // final Gson gson = new Gson();
    // final User u = createUser( "user1", 123456789, 0 );
    //
    // mvc.perform( post( "/api/v1/users" ).contentType(
    // MediaType.APPLICATION_JSON )
    // .content( TestUtils.asJsonString( u ) ) ).andExpect( status().isOk() );
    //
    // String response = mvc.perform( get( "/api/v1/users/user1" ) ).andExpect(
    // status().isOk() ).andReturn()
    // .getResponse().getContentAsString();
    //
    // final User responseU = gson.fromJson( response, User.class );
    //
    // assertEquals( u, responseU );
    //
    // // Test getting a non existing recipe
    // response = mvc.perform( get( "/api/v1/users/fake" ) ).andExpect(
    // status().is4xxClientError() ).andReturn()
    // .getResponse().getContentAsString();
    //
    // }

    @Test
    @Transactional
    public void testLoginUser () throws Exception {
        service.deleteAll();

        final User u = createUser2( "user1", "password", 0 );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u ) ) ).andExpect( status().isOk() );

        mvc.perform( get( "/api/v1/users/user1" ).contentType( MediaType.APPLICATION_JSON ).content( "password" ) )
                .andExpect( status().isOk() );

    }

    @Test
    @Transactional
    public void testDeleteUser () throws Exception {

        // Testing adding and deleting 1 user
        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Users in the CoffeeMaker" );

        final User u1 = createUser( "user1", 123456789, 0 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only be one user in the CoffeeMaker" );

        mvc.perform( delete( "/api/v1/users/user1" ) ).andExpect( status().isOk() );

        // Testing adding and deleting 2 users

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no users in the CoffeeMaker" );

        final User u2 = createUser( "user2", 123456789, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u2 ) ) ).andExpect( status().isOk() );

        final User u3 = createUser( "user3", 123456789, 2 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u3 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 2, service.findAll().size(), "There should only be two users in the CoffeeMaker" );

        mvc.perform( delete( "/api/v1/users/user2" ) ).andExpect( status().isOk() );
        mvc.perform( delete( "/api/v1/users/user3" ) ).andExpect( status().isOk() );

        // Testing adding and deleting 3 users

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no recipes in the CoffeeMaker" );

        final User u4 = createUser( "user4", 123456789, 0 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u4 ) ) ).andExpect( status().isOk() );

        final User u5 = createUser( "user5", 123456789, 1 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u5 ) ) ).andExpect( status().isOk() );

        final User u6 = createUser( "user6", 123456789, 2 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u6 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 3, service.findAll().size(), "There should only be three users in the CoffeeMaker" );

        mvc.perform( delete( "/api/v1/users/user4" ) ).andExpect( status().isOk() );
        mvc.perform( delete( "/api/v1/users/user5" ) ).andExpect( status().isOk() );
        mvc.perform( delete( "/api/v1/users/user6" ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no users in the CoffeeMaker" );

    }

    @Test
    @Transactional
    public void testConcurrentUsers () throws Exception {

        // Testing deletions of the same user by concurrent users

        Assertions.assertEquals( 0, service.findAll().size(), "There should be no Users in the CoffeeMaker" );

        final User u1 = createUser( "user1", 123456789, 0 );
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u1 ) ) ).andExpect( status().isOk() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should only be one user in the CoffeeMaker" );

        mvc.perform( delete( "/api/v1/users/user1" ) ).andExpect( status().isOk() );

        mvc.perform( delete( "/api/v1/users/user1" ) ).andExpect( status().is4xxClientError() );
    }

    private User createUser ( final String userName, final int password, final int permissions ) {
        final User user = new User();
        user.setUserName( userName );
        user.setPassword( password );
        user.setPermissions( permissions );
        return user;
    }

    private User createUser2 ( final String userName, final String password, final int permissions ) {
        final User user = new User( userName, password, permissions );
        return user;

    }

}
