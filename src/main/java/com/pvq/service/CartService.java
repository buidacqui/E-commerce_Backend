package com.pvq.service;

import com.pvq.exception.ProductException;
import com.pvq.model.Cart;
import com.pvq.model.User;
import com.pvq.request.AddItemRequest;

public interface CartService {

    public Cart createCart(User user);

    public String addCartItem(Long userId, AddItemRequest req) throws ProductException;

    public Cart findUserCart(Long userId);
}
