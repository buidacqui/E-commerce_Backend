package com.pvq.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.pvq.exception.CartItemException;
import com.pvq.exception.UserException;
import com.pvq.model.CartItem;
import com.pvq.model.User;
import com.pvq.response.ApiResponse;
import com.pvq.service.CartItemService;
import com.pvq.service.UserService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/cartitem")
public class CartItemController {
	@Autowired
	private CartItemService cartItemService;

	@Autowired
	private UserService userService;

	@DeleteMapping("/{cartItemId}")
	@Operation(description = "Remove Cart Item From Cart")
	@io.swagger.v3.oas.annotations.responses.ApiResponse(description = "Delete Item")
	public ResponseEntity<ApiResponse> deleteCartItem(
	        @PathVariable Long cartItemId,
	        @RequestHeader("Authorization") String jwt)
	        throws UserException, CartItemException {

	    User user = userService.findUserProfileByJwt(jwt);
	    cartItemService.removeCartItem(user.getId(), cartItemId);

	    ApiResponse res = new ApiResponse();
	    res.setMessage("delete item from cart");
	    res.setStatus(true);

	    return new ResponseEntity<>(res, HttpStatus.OK);
	}
	@PutMapping("/{cartItemId}")
	@Operation(description = "Update Item In Cart")
	public ResponseEntity<CartItem> updateCartItem(
	        @RequestBody CartItem cartItem,
	        @PathVariable Long cartItemId,
	        @RequestHeader("Authorization") String jwt)
	        throws UserException, CartItemException {

	    User user = userService.findUserProfileByJwt(jwt);

	    CartItem updatedCartItem = cartItemService.updateCartItem(user.getId(), cartItemId, cartItem);

	    return new ResponseEntity<>(updatedCartItem, HttpStatus.OK);
	}

}
