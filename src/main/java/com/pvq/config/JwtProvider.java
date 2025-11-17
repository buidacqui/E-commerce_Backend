package com.pvq.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtProvider {

    // Khóa bí mật >= 32 ký tự
    private static final String SECRET_KEY = JwtConstant.SECRET_KEY;

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // ✅ Tạo JWT token từ Authentication
    public String generateToken(Authentication auth) {
        String authorities = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        System.out.println("🔐 Generating JWT for user: " + auth.getName());
        System.out.println("🧩 Roles: " + authorities);

        return Jwts.builder()
                .setSubject(auth.getName()) // Email dùng làm subject
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000)) // 10 ngày
                .claim("email", auth.getName())
                .claim("authorities", authorities)
                .signWith(key)
                .compact();
    }

    // ✅ Lấy email từ JWT
    public String getEmailFromToken(String jwt) {
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        return (String) claims.get("email");
    }

    // ✅ Lấy roles từ JWT (bổ sung thêm để tiện debug / phân quyền)
    public String getAuthoritiesFromToken(String jwt) {
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
                .getBody();

        return (String) claims.get("authorities");
    }
}
