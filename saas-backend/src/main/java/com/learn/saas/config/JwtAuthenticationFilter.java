package com.learn.saas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.learn.saas.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String bearerToken = extractBearerToken(request);
        if(bearerToken != null && jwtUtil.validateToken(bearerToken)) {
            if (bearerToken != null && !bearerToken.isEmpty()) {
                String username = jwtUtil.extractUsername(bearerToken);
                String role = jwtUtil.extractRole(bearerToken);
                
                if (username != null && !username.isEmpty()) {
                    // Handle different role formats
                    if (role == null || role.isEmpty()) {
                        role = "USER";
                    }
                    
                    // Ensure ROLE_ prefix for Spring Security
                    String roleFormatted = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    
                    List<SimpleGrantedAuthority> authorities = 
                        List.of(new SimpleGrantedAuthority(roleFormatted));
                    
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("JWT authenticated - User: " + username + ", Authority: " + roleFormatted);
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
