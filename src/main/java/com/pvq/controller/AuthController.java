package com.pvq.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pvq.config.JwtProvider;
import com.pvq.exception.UserException;
import com.pvq.model.Role;
import com.pvq.model.User;
import com.pvq.repository.UserRepository;
import com.pvq.service.CartService;
import com.pvq.service.CustomUserServiceImplementation;
import com.pvq.request.LoginRequest;
import com.pvq.response.AuthResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserServiceImplementation customUserService;
    private final CartService cartService;

    public AuthController(UserRepository userRepository,
                          CustomUserServiceImplementation customUserService,
                          PasswordEncoder passwordEncoder,
                          JwtProvider jwtProvider,
                          CartService cartService) {
        this.userRepository = userRepository;
        this.customUserService = customUserService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.cartService = cartService;
    }

    // ======================== SIGNUP =========================
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws UserException {

        if (!user.getEmail().matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
            throw new UserException("📧 Email không hợp lệ! Vui lòng nhập đúng định dạng (ví dụ: ten@gmail.com)");
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserException("Email đã tồn tại: " + user.getEmail());
        }

        // Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Gán mặc định role & mobile nếu chưa có
        if (user.getRole() == null) {
            user.setRole(Role.ROLE_USER);
        }
        if (user.getMobile() == null) {
            user.setMobile("");
        }

        // Lưu user và tạo giỏ hàng
        User savedUser = userRepository.save(user);
        cartService.createCart(savedUser);

        // Tạo Authentication từ user
        UserDetails userDetails = customUserService.loadUserByUsername(savedUser.getEmail());
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Sinh JWT
        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Signup Success ✅");

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<Map<String, String>> handleUserException(UserException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // ======================== SIGNIN =========================
    @PostMapping("/signin")
    public ResponseEntity<?> loginUserHandler(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        try {
            Authentication authentication = authenticate(username, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtProvider.generateToken(authentication);
            AuthResponse authResponse = new AuthResponse(token, "Login Success ✅");

            return new ResponseEntity<>(authResponse, HttpStatus.OK);

        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Đăng nhập thất bại! Vui lòng thử lại sau."));
        }
    }

    // ======================== AUTH HELPER =========================
    private Authentication authenticate(String username, String password) {
        UserDetails userDetails;
        try {
            userDetails = customUserService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Email không tồn tại trong hệ thống!");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Mật khẩu không đúng!");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // ======================== UPDATE PROFILE =========================
    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody User updatedUser) {
        User existingUser = userRepository.findByEmail(updatedUser.getEmail());
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy người dùng!");
        }

        // Cập nhật thông tin cơ bản
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setMobile(updatedUser.getMobile());
        existingUser.setAddress(updatedUser.getAddress());

        userRepository.save(existingUser);

        return ResponseEntity.ok("Cập nhật hồ sơ thành công!");
    }
}
