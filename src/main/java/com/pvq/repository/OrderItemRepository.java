package com.pvq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.pvq.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {
	@Modifying
	@Transactional
	@Query("DELETE FROM OrderItem oi WHERE oi.product.id = :productId")
	void deleteByProductId(@Param("productId") Long productId);


}
