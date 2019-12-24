package com.example.demo.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

import com.example.demo.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
	private UserRepository userRepository = mock(UserRepository.class);
	private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    
    @Before
	public void setUp() {
		cartController = new CartController();
		TestUtils.injectObject(cartController, "userRepository", userRepository);
		TestUtils.injectObject(cartController, "cartRepository", cartRepository);
		TestUtils.injectObject(cartController, "itemRepository", itemRepository);
    }
    
    /**
     * Testing that the HTTP POST request addToCart for CartController.java 
     * adds the correct quantity of the selected items and returns a 200 response
     */
    @Test
	public void testAddToCart200PostRequest() {
		ModifyCartRequest request = new ModifyCartRequest();
		request.setItemId(0);
		request.setUsername("test");
		request.setQuantity(2);
		
		User user = new User();
		user.setId(0);
		user.setUsername("test");
		user.setPassword("testPassword");
		
		Cart cart = new Cart();
		user.setCart(cart);
		
		Item item = new Item();
		item.setId(0L);
		item.setName("Gold Bar");
        item.setDescription("A very big gold bar");
        
        BigDecimal price = BigDecimal.valueOf(2000.00);
		item.setPrice(price);
		Optional<Item> optionalItem = Optional.of(item);
		
		when(userRepository.findByUsername("test")).thenReturn(user);
		when(itemRepository.findById(0L)).thenReturn(optionalItem);
		
		ResponseEntity<Cart> response = cartController.addTocart(request);
		
		assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getItems().size());
        assertEquals("Gold Bar", response.getBody().getItems().get(0).getName());
    }
    
    /**
     * Testing an invalid POST request for item for the CartController class
     * A 404 HTTP status is expected when retrieving the cart items
     */
    @Test
	public void testAddToCart404PostRequestInvalidItem() {
		ModifyCartRequest request = new ModifyCartRequest();
		request.setItemId(0);
		request.setUsername("test");
		request.setQuantity(1);
		
		User user = new User();
		user.setId(0);
		user.setUsername("test");
		user.setPassword("testPassword");	
		Cart cart = new Cart();
		user.setCart(cart);
		
		when(userRepository.findByUsername("testt")).thenReturn(user);
		ResponseEntity<Cart> response = cartController.addTocart(request);
		assertEquals(404, response.getStatusCodeValue());
    }
    
    /**
     * Testing an invalid POST request for user for the CartController class
     * A 404 HTTP status is expected when retrieving the cart items
     */
    @Test
	public void testAddToCart404PostRequestInvalidUser()
	{
		ModifyCartRequest request = new ModifyCartRequest();
		request.setItemId(0);
		request.setUsername("test");
		request.setQuantity(2);
		
        User user  = null;
        
        Item item = new Item();
        item.setId(0L);
		item.setName("Gold Bar");
        item.setDescription("A very big gold bar");
        
        BigDecimal price = BigDecimal.valueOf(2000.00);
		item.setPrice(price);
		Optional<Item> optionalItem = Optional.of(item);
		
		when(userRepository.findByUsername("test")).thenReturn(user);
		when(itemRepository.findById(0L)).thenReturn(optionalItem);
		ResponseEntity<Cart> response = cartController.addTocart(request);
		assertEquals(404, response.getStatusCodeValue());
    }
    
    /**
     * Testing that the HTTP POST request removeFromCart for CartController.java 
     * removes the last inserted item in the cart and returns a 200 status
     */
    @Test
	public void testRemoveFromCart200PostRequest()
	{
		ModifyCartRequest request = new ModifyCartRequest();
		request.setItemId(0);
		request.setUsername("test");
		request.setQuantity(1);
		User user = new User();
		user.setId(0);
		user.setUsername("test");
		user.setPassword("testPassword");
		Cart cart = new Cart();
		cart.setId(0L);
		cart.setUser(user);
        List<Item> items = cart.getItems();
        
        // Check if the cart is empty
        if(items == null)
            items = new ArrayList<Item>();
		
		user.setCart(cart);
		
		Item item = new Item();
		item.setId(0L);
		item.setName("Gold Bar");
		item.setDescription("A very big gold bar.");
		BigDecimal price = BigDecimal.valueOf(2000.00);
		item.setPrice(price);
        
        // Add two gold bars to the cart
		items.add(item);
        items.add(item);
        Long newTotal = price.longValue() * items.size();
		cart.setItems(items);
		cart.setTotal(BigDecimal.valueOf(newTotal));
		
		Optional<Item> optionalItem = Optional.of(item);
		when(userRepository.findByUsername("test")).thenReturn(user);
		when(itemRepository.findById(0L)).thenReturn(optionalItem);
        
        // Remove one of the items from the cart
		ResponseEntity<Cart> response = cartController.removeFromcart(request);
		assertEquals(200, response.getStatusCodeValue());
		assertEquals(items.size(), response.getBody().getItems().size());
    }
	
	/**
     * Testing that the HTTP POST request removeFromCart for CartController.java 
     * returns a 404 status if passed an invalid user
     */
    @Test
	public void testRemoveFromCart404PostRequestInvalidUser()
	{
		ModifyCartRequest request = new ModifyCartRequest();
		request.setItemId(0);
		request.setUsername("test");
		request.setQuantity(1);
		User user = null; //uninitialized user
        
        Item item = new Item();
		item.setId(0L);
		item.setName("Gold Bar");
		item.setDescription("A very big gold bar.");
		BigDecimal price = BigDecimal.valueOf(2000.00);
		item.setPrice(price);
		Optional<Item> optionalItem = Optional.of(item);
		
		when(userRepository.findByUsername("test")).thenReturn(user);
		when(itemRepository.findById(0L)).thenReturn(optionalItem);
		ResponseEntity<Cart> response = cartController.removeFromcart(request);
		assertEquals(404, response.getStatusCodeValue());
    }
	
	/**
     * Testing that the HTTP POST request removeFromCart for CartController.java 
     * returns a 404 status if passed an invalid item
     */
    @Test
	public void testRemoveFromCart404PostRequestInvalidItem()
	{
		ModifyCartRequest request = new ModifyCartRequest();
		request.setItemId(0);
		request.setUsername("test");
		request.setQuantity(1);
		User user = new User();
		user.setId(0);
		user.setUsername("test");
		user.setPassword("testPassword");
		
		Cart cart = new Cart();
		cart.setId(0L);
		cart.setUser(user);
		List<Item> items = cart.getItems();
		// Check if the cart is empty
		if(items == null)
			items = new ArrayList<Item>();
		
		user.setCart(cart);
		
		Item item = null;
		// Add two non-existent items
		items.add(item);
		items.add(item);
		cart.setItems(items);
		cart.setTotal(BigDecimal.valueOf(0));
		
		Optional<Item> optionalItem = Optional.ofNullable(item);
		when(userRepository.findByUsername("test")).thenReturn(user);
		when(itemRepository.findById(0L)).thenReturn(optionalItem);
		ResponseEntity<Cart> response = cartController.removeFromcart(request);
		assertEquals(404, response.getStatusCodeValue());
	}
}