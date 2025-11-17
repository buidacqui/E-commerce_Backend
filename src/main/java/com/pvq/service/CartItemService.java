package com.pvq.service;

import com.pvq.exception.CartItemException;
import com.pvq.exception.UserException;
import com.pvq.model.Cart;
import com.pvq.model.CartItem;
import com.pvq.model.Product;

public interface CartItemService {
	public CartItem createCartItem(CartItem cartItem);

	public CartItem updateCartItem(Long userId, Long id, CartItem cartItem)
	        throws CartItemException, UserException;
	
    public CartItem isCartItemExist(Cart cart, Product product, String size, Long userId);
    
    public void removeCartItem(Long userId, Long cartItemId) throws CartItemException, UserException;

    public CartItem findCartItemById(Long cartItemId) throws CartItemException;

    public CartItem updateCartItem(CartItem cartItem);

}
