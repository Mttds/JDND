package com.example.demo.controllers;

import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.TestUtils;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import junit.framework.Assert;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {

        userController = new UserController();
        /*
            Looks for the objects create in the class UserController 
            1. Object target
            2. String fieldName
            3. Object toInject
        */
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);

    }

    @Test
    public void testCreateUserHappyPath() throws Exception {

        when(encoder.encode("testPassword")).thenReturn("thisIsHashed"); 
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("test");
        userRequest.setPassword("testPassword");
        userRequest.setConfirmPassword("testPassword");

        final ResponseEntity<User> response = userController.createUser(userRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());

    }

    /**
     * Tests a GET HTTP request passing the userName
     */

    @Test
	public void testGetRequestUserName()
	{
		String username = "test";
		Cart cart = new Cart();	
        User user = new User();
        
		user.setId(0);
		user.setUsername(username);
		user.setPassword("thisIsHashed");
		user.setCart(cart);
		
		when(userRepository.findByUsername("test")).thenReturn(user);
		ResponseEntity<User> response = userController.findByUserName(username);
		assertNotNull(response);
		assertEquals(200, response.getStatusCodeValue());
        user = response.getBody();
        
		Assert.assertEquals("test", user.getUsername());
		assertEquals(0, user.getId());
	}

    /**
     * Tests that the confirmPassword matches with the password selected by the user
     */
    @Test
	public void testCreateUserPassword()
	{
		when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
		
		CreateUserRequest userRequest = new CreateUserRequest();
		userRequest.setUsername("test");
		userRequest.setPassword("testPassword");
		userRequest.setConfirmPassword("testpassword");
		
		final ResponseEntity<User> response = userController.createUser(userRequest);
		
		assertNotNull(response);
		assertEquals(400, response.getStatusCodeValue());
	}


}