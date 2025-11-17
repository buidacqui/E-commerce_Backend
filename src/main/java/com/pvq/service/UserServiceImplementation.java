package com.pvq.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.pvq.config.JwtProvider;
import com.pvq.exception.UserException;
import com.pvq.model.Role;
import com.pvq.model.User;
import com.pvq.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserServiceImplementation implements UserService {
	
	private UserRepository userRepository;
	private JwtProvider jwtProvider;

    public UserServiceImplementation(UserRepository userRepository, JwtProvider jwtProvider) {
	    this.userRepository = userRepository;
	    this.jwtProvider = jwtProvider;
	}

	@Override
	public User findUserById(Long userId) throws UserException {
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
		    return user.get();
		}

		throw new UserException("user not found with id : " + userId);
		}

		@Override
		public User findUserProfileByJwt(String jwt) throws UserException {
		    String email = jwtProvider.getEmailFromToken(jwt);

		    User user = userRepository.findByEmail(email);

		    if (user == null) {
		        throw new UserException("user not found with email : " + email);
		    }

		    return user;
		}
		@Override
	    public List<User> getAllUsers() {
	        return userRepository.findAll();
	    }

	    @Override
	    public User getUserById(Long id) {
	        return userRepository.findById(id)
	                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng có ID: " + id));
	    }

	    @Override
	    public User updateUserRole(Long id, String newRole) {
	        User user = getUserById(id);
	        user.setRole(Role.valueOf(newRole)); // ví dụ: ROLE_ADMIN hoặc ROLE_USER
	        return userRepository.save(user);
	    }

	    @Override
	    public void deleteUser(Long id) {
	        if (!userRepository.existsById(id)) {
	            throw new EntityNotFoundException("Người dùng không tồn tại");
	        }
	        userRepository.deleteById(id);
	    }
}
 