package com.pvq.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.pvq.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String email);
}

