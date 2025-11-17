package com.pvq.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pvq.exception.CartItemException;
import com.pvq.exception.UserException;
import com.pvq.model.Cart;
import com.pvq.model.CartItem;
import com.pvq.model.Product;
import com.pvq.model.User;
import com.pvq.repository.CartItemRepository;
import com.pvq.repository.CartRepository;

@Service
public class CartItemServiceImplementation implements CartItemService {
	
	private CartItemRepository cartItemRepository;
	private UserService userService;
	private CartRepository cartRepository;

	public CartItemServiceImplementation(CartItemRepository cartItemRepository,
	                                     UserService userService,
	                                     CartRepository cartRepository) {
	    this.cartItemRepository = cartItemRepository;
	    this.userService = userService;
	    this.cartRepository = cartRepository;
	}

	@Override
	public CartItem createCartItem(CartItem cartItem) {
	    // Nếu chưa có quantity, mặc định là 1
		if (cartItem.getQuantity() <= 0) {
		    cartItem.setQuantity(1);
		}

	    // Tính lại giá dựa trên số lượng
	    cartItem.setPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());
	    cartItem.setDiscountedPrice(cartItem.getProduct().getDiscountedPrice() * cartItem.getQuantity());

	    return cartItemRepository.save(cartItem);
	}



	@Override
	public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws CartItemException, UserException {

	    CartItem item = findCartItemById(id);
	    User user = userService.findUserById(item.getUserId());

	    if (user.getId().equals(userId)) {
	        item.setQuantity(cartItem.getQuantity());
	        item.setPrice(item.getQuantity() * item.getProduct().getPrice());
	        item.setDiscountedPrice(item.getProduct().getDiscountedPrice() * item.getQuantity());
	    }

	    return cartItemRepository.save(item);
	}
	

	@Override
	public CartItem isCartItemExist(Cart cart, Product product, String size, Long userId) {
	    CartItem cartItem = cartItemRepository.isCartItemExist(cart, product, size, userId);
	    return cartItem;
	}

	@Override
	public void removeCartItem(Long userId, Long cartItemId) throws CartItemException, UserException {
	    CartItem cartItem = findCartItemById(cartItemId);

	    User user = userService.findUserById(cartItem.getUserId());
	    User reqUser = userService.findUserById(userId);

	    if(user.getId().equals(reqUser.getId())) {
	        cartItemRepository.deleteById(cartItemId);
	    }
	    else {
	        throw new UserException("you can't remove another users item");
	    }
	}


	@Override
	public CartItem findCartItemById(Long cartItemId) throws CartItemException {
	    Optional<CartItem> opt = cartItemRepository.findById(cartItemId);

	    if (opt.isPresent()) {
	        return opt.get();
	    }
	    throw new CartItemException("cartItem not found with id : " + cartItemId);
	}
	
	@Override
	public CartItem updateCartItem(CartItem cartItem) {
	    return cartItemRepository.save(cartItem);
	}

}
