package com.tl.hotelproject.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().contains("/api/v1/users/login") ||
                request.getRequestURI().equals("/api/refresh") ||
                request.getRequestURI().equals("/api/save") ||
                request.getRequestURI().equals("/api/forget-password") ||
                request.getRequestURI().contains("/api/user/active") ||
                request.getRequestURI().contains("/api/v1/bill/ipn-vnpay")){
            filterChain.doFilter(request, response);
        }else{
            String header = request.getHeader(AUTHORIZATION);
            System.out.println(header + "longtv");
            if(header != null && header.startsWith("Bearer ")){
                try {
                    String token = header.substring("Bearer ".length());
                    Algorithm algorithm = Algorithm.HMAC256("hotel".getBytes(StandardCharsets.UTF_8));
                    JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = jwtVerifier.verify(token);
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(decodedJWT.getKeyId(), null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    filterChain.doFilter(request, response);
                }catch (Exception e){
                    response.setHeader("error", e.getMessage());
                    System.out.println(e.getMessage());
                    Map<String, String> tokens = new HashMap<>();
                    tokens.put("error_message", e.getMessage() + "test deploy");
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                }
            }else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
