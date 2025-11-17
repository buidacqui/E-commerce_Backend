	package com.pvq.service;

import java.util.*;

import com.pvq.exception.OrderException;
import com.pvq.model.Address;
import com.pvq.model.Order;
import com.pvq.model.User;
 
public interface OrderService {
	
	public Order createOrder(User user,Address shippingAdress);
	
	public Order findOrderById(Long orderId) throws OrderException;

	public List<Order> usersOrderHistory(Long userId);

	public Order placedOrder(Long orderId) throws OrderException;

	public Order confirmedOrder(Long orderId) throws OrderException;

	public Order shippedOrder(Long orderId) throws OrderException;

	public Order deliveredOrder(Long orderId) throws OrderException;

	public Order canceledOrder(Long orderId) throws OrderException;

	public List<Order> getAllOrders();

	public void deleteOrder(Long orderId) throws OrderException;
	
	public List<Order> findOrdersByProductId(Long productId);

}
