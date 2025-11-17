package com.pvq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pvq.exception.OrderException;
import com.pvq.exception.UserException;
import com.pvq.model.Address;
import com.pvq.model.Order;
import com.pvq.model.User;
import com.pvq.service.OrderService;
import com.pvq.service.UserService;

@RestController
@RequestMapping("api/orders")
public class OrderController {
	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	@PostMapping("/")
	public ResponseEntity<Order> createOrder(
	        @RequestBody Address shippingAddress,
	        @RequestHeader("Authorization") String jwt
	) throws UserException {

	    User user = userService.findUserProfileByJwt(jwt);

	    Order order = orderService.createOrder(user, shippingAddress);

	    System.out.println("order " + order);

	    return new ResponseEntity<Order>(order, HttpStatus.CREATED);
	}

	@GetMapping("/user")
	public ResponseEntity<List<Order>> usersOrderHistory(
	        @RequestHeader("Authorization") String jwt
	) throws UserException {

	    User user = userService.findUserProfileByJwt(jwt);

	    List<Order> orders = orderService.usersOrderHistory(user.getId());

	    return new ResponseEntity<>(orders, HttpStatus.CREATED);
	}

	@GetMapping("/{Id}")
	public ResponseEntity<Order> findOrderById(
	        @PathVariable("Id") Long orderId,
	        @RequestHeader("Authorization") String jwt
	) throws UserException, OrderException {

	    User user = userService.findUserProfileByJwt(jwt);

	    Order order = orderService.findOrderById(orderId);
	    
	    System.out.println("order"+order);
	    
	    return new ResponseEntity<>(order, HttpStatus.ACCEPTED);
	}

}
