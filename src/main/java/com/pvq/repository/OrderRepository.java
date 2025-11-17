package com.pvq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pvq.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
	
	@Query("SELECT o FROM Order o WHERE o.user.id = :userId")
	public List<Order> getUsersOrders(@Param("userId") Long userId);

    
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems i WHERE i.product.id = :productId")
    List<Order> findOrdersByProductId(Long productId);
}
