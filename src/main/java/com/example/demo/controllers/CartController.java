package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	
	private static final Logger logger = LoggerFactory.getLogger(CartController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		logger.info("Adding item to cart for user " + request.getUsername() + ".");
		if(user == null) {
			logger.info("User " + request.getUsername() + " not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		logger.info("Checking if item with id " + request.getItemId() + " exists.");
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			logger.info("Item with id " + request.getItemId() + " not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);
		logger.info("Cart successfully updated with " + request.getQuantity() + " item(s) with id " + request.getItemId() + ".");
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		logger.info("Removing item from cart for user " + request.getUsername() + ".");
		if(user == null) {
			logger.info("User " + request.getUsername() + " not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			logger.info("Item with id " + request.getItemId() + " not found.");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);
		logger.info("Successfully removed from the cart " + request.getQuantity() + " item(s) with id " + request.getItemId() + ".");
		return ResponseEntity.ok(cart);
	}
		
}
