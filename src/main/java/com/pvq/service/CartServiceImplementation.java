package com.pvq.service;

import org.springframework.stereotype.Service;

import com.pvq.exception.ProductException;
import com.pvq.model.Cart;
import com.pvq.model.CartItem;
import com.pvq.model.Product;
import com.pvq.model.User;
import com.pvq.repository.CartRepository;
import com.pvq.request.AddItemRequest;
import com.pvq.service.CartItemService;


@Service
public class CartServiceImplementation implements CartService {
	
	private CartRepository cartRepository;
	private CartItemService cartItemService;
	private ProductService productService;

	public CartServiceImplementation(CartRepository cartRepository,
	                                 CartItemService cartItemService,
	                                 ProductService productService) {
	    this.cartRepository = cartRepository;
	    this.cartItemService = cartItemService;
	    this.productService = productService;
	}


	@Override
	public Cart createCart(User user) {
	    Cart cart = new Cart();
	    cart.setUser(user);
	    return cartRepository.save(cart);
	}

	@Override
	public String addCartItem(Long userId, AddItemRequest req) throws ProductException {
	    Cart cart = cartRepository.findByUserId(userId);
	    if (cart == null) {
	        User user = new User();
	        user.setId(userId);
	        cart = createCart(user);
	    }

	    Product product = productService.findProductById(req.getProductId());

	    CartItem isPresent = cartItemService.isCartItemExist(cart, product, req.getSize(), userId);

	    if (isPresent == null) {
	        // ✅ Trường hợp sản phẩm chưa có: thêm mới
	        CartItem cartItem = new CartItem();
	        cartItem.setProduct(product);
	        cartItem.setCart(cart);
	        cartItem.setQuantity(req.getQuantity());
	        cartItem.setUserId(userId);

	        int price = req.getQuantity() * product.getDiscountedPrice();
	        cartItem.setPrice(price);
	        cartItem.setSize(req.getSize());

	        CartItem createdCartItem = cartItemService.createCartItem(cartItem);
	        cart.getCartItems().add(createdCartItem);
	    } else {
	        // ✅ Trường hợp sản phẩm đã có trong giỏ: tăng số lượng
	        int newQuantity = isPresent.getQuantity() + req.getQuantity();
	        isPresent.setQuantity(newQuantity);

	        int newPrice = newQuantity * product.getDiscountedPrice();
	        isPresent.setPrice(newPrice);

	        try {
	            cartItemService.updateCartItem(userId, isPresent.getId(), isPresent);
	        } catch (Exception e) {
	            e.printStackTrace();
	            throw new RuntimeException("Không thể cập nhật giỏ hàng: " + e.getMessage());
	        }	    }

	    return "Item Added To Cart";
	}




	@Override
	public Cart findUserCart(Long userId) {
	    Cart cart = cartRepository.findByUserId(userId);

	    int totalPrice = 0;
	    int totalDiscountedPrice = 0;
	    int totalItem = 0;

	    for (CartItem cartItem : cart.getCartItems()) {
	        totalPrice = totalPrice + cartItem.getPrice();
	        totalDiscountedPrice = totalDiscountedPrice + cartItem.getDiscountedPrice();
	        totalItem = totalItem + cartItem.getQuantity();
	    }

	    cart.setTotalDiscountedPrice(totalDiscountedPrice);
	    cart.setTotalItem(totalItem);
	    cart.setTotalPrice(totalPrice);
	    cart.setDiscounte(totalPrice-totalDiscountedPrice);

	    return cartRepository.save(cart);
	}

}
