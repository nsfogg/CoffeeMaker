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
import edu.ncsu.csc.CoffeeMaker.controllers.DTO.NamePasswordPermissionUserDTO;
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

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new NamePasswordPermissionUserDTO( "user1", "password", 0, u ) ) ) )
                .andExpect( status().isOk() );

        User fetchedUser = service.findByName( "user1" );
        assertEquals( 2, service.count() );
        assertEquals( "user1", fetchedUser.getUserName() );
        assertEquals( 0, fetchedUser.getPermissions().intValue() );
        assertEquals( User.hashPassword( "password" ), fetchedUser.getPassword() );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new NamePasswordPermissionUserDTO( "user1", "password", 0, u ) ) ) )
                .andExpect( status().isConflict() );

        assertEquals( 2, service.count() );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString(
                        new NamePasswordPermissionUserDTO( "user2", "complexPassword", 0, new User() ) ) ) )
                .andExpect( status().isOk() );

        fetchedUser = service.findByName( "user2" );
        assertEquals( 3, service.count() );
        assertEquals( "user2", fetchedUser.getUserName() );
        assertEquals( 0, fetchedUser.getPermissions().intValue() );
        assertEquals( User.hashPassword( "complexPassword" ), fetchedUser.getPassword() );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString(
                        new NamePasswordPermissionUserDTO( "barista", "badpassword", 1, new User() ) ) ) )
                .andExpect( status().isForbidden() );

        assertEquals( 3, service.count() );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON ).content(
                TestUtils.asJsonString( new NamePasswordPermissionUserDTO( "barista", "badPassword", 1, u ) ) ) )
                .andExpect( status().isOk() );

        fetchedUser = service.findByName( "barista" );
        assertEquals( 4, service.count() );
        assertEquals( "barista", fetchedUser.getUserName() );
        assertEquals( 1, fetchedUser.getPermissions().intValue() );
        assertEquals( User.hashPassword( "badPassword" ), fetchedUser.getPassword() );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils
                        .asJsonString( new NamePasswordPermissionUserDTO( "manager", "iamthebest", 2, new User() ) ) ) )
                .andExpect( status().isForbidden() );

        assertEquals( 4, service.count() );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON ).param( "userName", "manager" )
                .param( "password", "iamthebest" ).param( "permission", "2" ).content(
                        TestUtils.asJsonString( new NamePasswordPermissionUserDTO( "manager", "iamthebest", 2, u ) ) ) )
                .andExpect( status().isOk() );

        fetchedUser = service.findByName( "manager" );
        assertEquals( 5, service.count() );
        assertEquals( "manager", fetchedUser.getUserName() );
        assertEquals( 2, fetchedUser.getPermissions().intValue() );
        assertEquals( User.hashPassword( "iamthebest" ), fetchedUser.getPassword() );

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

        // create the user
        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new NamePasswordPermissionUserDTO( "user1", "password", 0, u ) ) ) )
                .andExpect( status().isOk() );
        // log in
        mvc.perform( get( "/api/v1/users/user1/password" ) ).andExpect( status().isOk() );
        // now use an invalid user
        mvc.perform( get( "/api/v1/users/user2/password" ) ).andExpect( status().isNotFound() );
        // now use an invalid passowrd
        mvc.perform( get( "/api/v1/users/user1/notmypassword" ) ).andExpect( status().isNotFound() );

    }

    @Test
    @Transactional
    public void testDeleteUser () throws Exception {

        // Testing adding and deleting 1 user
        Assertions.assertEquals( 1, service.findAll().size(), "There should be only the admin in CoffeeMaker" );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new NamePasswordPermissionUserDTO( "user1", "password", 0, u ) ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 2, service.findAll().size(), "There should only be 2 user in the CoffeeMaker" );

        mvc.perform( delete( "/api/v1/users/user1" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u ) ) ).andExpect( status().isOk() );

        // Testing adding and deleting 2 users

        Assertions.assertEquals( 1, service.findAll().size(), "There should be one users in the CoffeeMaker" );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new NamePasswordPermissionUserDTO( "user2", "password", 1, u ) ) ) )
                .andExpect( status().isOk() );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new NamePasswordPermissionUserDTO( "user3", "password", 2, u ) ) ) )
                .andExpect( status().isOk() );

        Assertions.assertEquals( 3, service.findAll().size(), "There should only be two users in the CoffeeMaker" );

        // test an unauthorized deletion

        mvc.perform( delete( "/api/v1/users/user2" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( new User() ) ) ).andExpect( status().isForbidden() );

        mvc.perform( delete( "/api/v1/users/user2" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u ) ) ).andExpect( status().isOk() );
        mvc.perform( delete( "/api/v1/users/user3" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u ) ) ).andExpect( status().isOk() );

        // Testing deleting a fake users

        Assertions.assertEquals( 1, service.findAll().size(), "There should be no recipes in the CoffeeMaker" );

        mvc.perform( delete( "/api/v1/users/user3" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u ) ) ).andExpect( status().isNotFound() );

        Assertions.assertEquals( 1, service.findAll().size(), "There should be no users in the CoffeeMaker" );

    }
}
