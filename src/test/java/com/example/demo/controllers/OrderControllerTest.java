package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.demo.TestUtils;

public class OrderControllerTest {

    private OrderController orderController;
	private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    
    @Before
	public void setup()
	{
		orderController = new OrderController();
		TestUtils.injectObject(orderController, "userRepository", userRepository);
		TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }

    /**
     * Testing that the HTTP POST request submit for OrderController.java 
     * throws a 404 http status with an invalid user
     */
    @Test
	public void testSubmit404PostRequestInvalidUser()
	{
		User user  = null;
		when(userRepository.findByUsername("test")).thenReturn(user);
		ResponseEntity<UserOrder> response = orderController.submit("test");
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
    }
    
    /**
     * Testing that the HTTP POST request getOrdersForUser for OrderController.java 
     * throws a 404 http status with an invalid user
     */
    @Test
	public void testGetOrdersForUser404PostRequestInvalidUser()
	{
		User user  = null;
		Cart cart = new Cart();
		cart.setId(0L);
		cart.setUser(user);
		
        Item item = new Item();
		item.setId(0L);
		item.setName("Gold Bar");
        item.setDescription("A very big gold bar");
        BigDecimal price = BigDecimal.valueOf(2000.00);
		item.setPrice(price);
		
        List<Item> items = new ArrayList<Item>();
        items.add(item);
        items.add(item);
        cart.setItems(items);
        Long newTotal = price.longValue() * items.size();
		cart.setTotal(BigDecimal.valueOf(newTotal));
		
		when(userRepository.findByUsername("test")).thenReturn(user);
		UserOrder userOrder = UserOrder.createFromCart(cart);
		List<UserOrder> userOrders = Arrays.asList(userOrder);
		when(orderRepository.findByUser(user)).thenReturn(userOrders);
		
		ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
		assertNotNull(response);
		assertEquals(404, response.getStatusCodeValue());
	}
    
    /**
     * Testing that the HTTP POST request submit for OrderController.java 
     * persists the cart in the UserOrder repository
     */
    @Test
	public void testSubmit200PostRequest()
	{
		User user  = new User();
		user.setId(0L);
		user.setUsername("test");
		user.setPassword("testPassword");
		Cart cart = new Cart();
		cart.setId(0L);
		cart.setUser(user);
		
		Item item = new Item();
		item.setId(0L);
		item.setName("Gold Bar");
        item.setDescription("A very big gold bar");
        BigDecimal price = BigDecimal.valueOf(2000.00);
		item.setPrice(price);
		
        List<Item> items = new ArrayList<Item>();
        items.add(item);
        items.add(item);
        cart.setItems(items);
        Long newTotal = price.longValue() * items.size();
		cart.setTotal(BigDecimal.valueOf(newTotal));
		user.setCart(cart);
		
		when(userRepository.findByUsername("test")).thenReturn(user);
		ResponseEntity<UserOrder> response = orderController.submit("test");
		assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test", response.getBody().getUser().getUsername());
		assertEquals(cart.getTotal(), response.getBody().getTotal());
		assertEquals(2, response.getBody().getItems().size());
    }
    
    /**
     * Testing that the HTTP POST request getOrdersForUser for OrderController.java 
     * returns a List containing the cart items (UserOrder Entity) of the given user
     */
    @Test
	public void testGetOrdersForUser200PostRequest()
	{
		User user  = new User();
		user.setId(0L);
		user.setUsername("test");
		user.setPassword("testPassword");
		
		Cart cart = new Cart();
		cart.setId(0L);
		cart.setUser(user);
		
        Item item = new Item();
		item.setId(0L);
		item.setName("Gold Bar");
        item.setDescription("A very big gold bar");
        BigDecimal price = BigDecimal.valueOf(2000.00);
        item.setPrice(price);
        List<Item> items = new ArrayList<Item>();
        items.add(item);
        items.add(item);
        cart.setItems(items);
        Long newTotal = price.longValue() * items.size();
		cart.setTotal(BigDecimal.valueOf(newTotal));
		user.setCart(cart);
		
		when(userRepository.findByUsername("test")).thenReturn(user);
		UserOrder userOrder = UserOrder.createFromCart(cart);
		List<UserOrder> userOrders = Arrays.asList(userOrder);
		when(orderRepository.findByUser(user)).thenReturn(userOrders);
		ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
		
		assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("test", response.getBody().get(0).getUser().getUsername());
		assertEquals(cart.getTotal(), response.getBody().get(0).getTotal());
		assertEquals(2, response.getBody().get(0).getItems().size());		
	}
}