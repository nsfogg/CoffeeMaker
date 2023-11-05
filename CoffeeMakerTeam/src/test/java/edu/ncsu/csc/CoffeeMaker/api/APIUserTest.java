package edu.ncsu.csc.CoffeeMaker.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.google.gson.Gson;

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

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        service.deleteAll();
    }

    @Test
    @Transactional
    public void ensureUser () throws Exception {
        service.deleteAll();

        final User u = new User();

        u.setUserName( "user1" );
        u.setPassword( 123456789 );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u ) ) ).andExpect( status().isOk() );

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

    @Test
    @Transactional
    public void testGetUserByNameAPI () throws Exception {
        final Gson gson = new Gson();
        final User u = createUser( "user1", 123456789, 0 );

        mvc.perform( post( "/api/v1/users" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( u ) ) ).andExpect( status().isOk() );

        String response = mvc.perform( get( "/api/v1/users/user1" ) ).andExpect( status().isOk() ).andReturn()
                .getResponse().getContentAsString();

        final User responseU = gson.fromJson( response, User.class );

        assertEquals( u, responseU );

        // Test getting a non existing recipe
        response = mvc.perform( get( "/api/v1/users/fake" ) ).andExpect( status().is4xxClientError() ).andReturn()
                .getResponse().getContentAsString();

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

}
