package com.pvq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pvq.model.Address;
import com.pvq.model.User;

public interface AddressRepository extends JpaRepository<Address, Long> {
	List<Address> findByUser(User user);

}
