package com.pvq.service;

import com.pvq.model.User;

import java.util.List;

import com.pvq.exception.UserException;

public interface UserService {

    public User findUserById(Long userId) throws UserException;

    public User findUserProfileByJwt(String jwt) throws UserException;
    
    public List<User> getAllUsers();
    public User getUserById(Long id);
    public User updateUserRole(Long id, String newRole);
    public void deleteUser(Long id);
}
