package com.pvq.controller;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.pvq.exception.ProductException;
import com.pvq.exception.UserException;
import com.pvq.model.Cart;
import com.pvq.model.User;
import com.pvq.request.AddItemRequest;
import com.pvq.response.ApiResponse;
import com.pvq.service.CartService;
import com.pvq.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/cart")
@Tag(name="Cart Management", description= "find user cart, add item to cart")
public class CartController {
	  @Autowired
	    private CartService cartService;

	    @Autowired
	    private UserService userService;

	    @GetMapping("/")
	    @Operation(description = "find cart by user id")
	    public ResponseEntity<Cart> findUserCart(@RequestHeader("Authorization") String jwt) throws UserException {
	        User user = userService.findUserProfileByJwt(jwt);
	        Cart cart = cartService.findUserCart(user.getId());
	        return new ResponseEntity<>(cart, HttpStatus.OK);
	    }
	    @PutMapping("/add")
	 // @Operation(description = "add item to cart")
	 public ResponseEntity<ApiResponse> addItemToCart(
	         @RequestBody AddItemRequest req,
	         @RequestHeader("Authorization") String jwt
	 ) throws UserException, ProductException {

	     User user = userService.findUserProfileByJwt(jwt);

	     cartService.addCartItem(user.getId(), req);

	     ApiResponse res = new ApiResponse();
	     res.setMessage("item added to cart");
	     res.setStatus(true);

	     return new ResponseEntity<>(res, HttpStatus.OK);
	 }

}
