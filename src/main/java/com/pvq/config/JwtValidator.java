package com.pvq.config;
import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtValidator extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
                                    throws ServletException, IOException {
        
        // Lấy JWT từ header Authorization
    	String jwt = request.getHeader(JwtConstant.JWT_HEADER);

    	 if (jwt == null || !jwt.startsWith("Bearer ")) {
    	        filterChain.doFilter(request, response);
    	        return;
    	    }
    	    jwt = jwt.substring(7); // bỏ đi prefix "Bearer "
    	    try {
    	        // Tạo key từ SECRET_KEY
    	        SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    	        // Giải mã và parse token
    	        Claims claims = Jwts.parserBuilder()
    	                .setSigningKey(key)
    	                .build()
    	                .parseClaimsJws(jwt)
    	                .getBody();

    	        // Lấy email từ payload
    	        String email = String.valueOf(claims.get("email"));

    	        // Lấy authorities (roles/quyền) từ payload
    	        String authorities = String.valueOf(claims.get("authorities"));
    	        List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

    	        Authentication authentication = new UsernamePasswordAuthenticationToken(
    	                email, // principal (username)
    	                null,  // credentials (mật khẩu không cần vì JWT đã xác thực rồi)
    	                auths  // danh sách quyền (roles)
    	        );

    	        SecurityContextHolder.getContext().setAuthentication(authentication);


    	    } catch (Exception e) { 
    	        System.out.println("⚠️ Invalid token... from JwtValidator: " + e.getMessage());
    	    }
    	
    	
    	filterChain.doFilter(request, response);
    }
}
