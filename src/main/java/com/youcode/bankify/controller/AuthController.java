package com.youcode.bankify.controller;

import com.youcode.bankify.dto.ErrorResponse;
import com.youcode.bankify.dto.LoginRequest;
import com.youcode.bankify.dto.RegisterRequest;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.service.AuthService;
import com.youcode.bankify.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(AuthService authService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        String response = authService.register(registerRequest);
        Map<String, String> responseBody = new HashMap<>();

        if (response.equals("User registered successfully")) {
            responseBody.put("message", "User registered successfully");
            return ResponseEntity.ok(responseBody);
        } else {
            responseBody.put("message", response);
            return ResponseEntity.badRequest().body(responseBody);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
       try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            final User user = authService.getUserByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            final String accessToken = jwtUtil.generateToken(user);

            final String refreshToken = jwtUtil.generateRefreshToken(user);

            if(accessToken == null || accessToken.split("\\.").length != 3){
                return ResponseEntity.status(500).body("Internal server error: Invalid JWT format");
            }

            Map<String , String > response = new HashMap<>();
            response.put("token", accessToken);
            response.put("refreshToken", refreshToken);
            return ResponseEntity.ok(response);
       } catch (AuthenticationException e){
           return ResponseEntity.status(401).body("Invalid username or password");
       }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")){
            return ResponseEntity.status(401).body("Refresh token is missing or invalid.");
        }

        String refreshToken = authorizationHeader.substring(7);

        try{
            String username = jwtUtil.extractUsername(refreshToken);
            if(jwtUtil.isTokenExpired(refreshToken)){
                return ResponseEntity.status(401).body("Refresh token is expired.");
            }

            final String newAccessToken = jwtUtil.generateToken(authService.getUserByUsername(username).orElseThrow());
            return ResponseEntity.ok(newAccessToken);
        }catch(Exception e){
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}
