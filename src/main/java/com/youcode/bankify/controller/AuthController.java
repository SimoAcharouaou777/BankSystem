package com.youcode.bankify.controller;

import com.youcode.bankify.dto.ErrorResponse;
import com.youcode.bankify.dto.LoginRequest;
import com.youcode.bankify.dto.RegisterRequest;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.service.AuthService;
import com.youcode.bankify.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
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
        String response = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if (response.equals("Authentication successful")) {
            Optional<User> optionalUser = authService.getUserByUsername(loginRequest.getUsername());
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(401).body(new ErrorResponse("User not found"));
            }
            User user = optionalUser.get();
            String token = jwtUtil.generateToken(user);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "User logged in successfully");
            responseBody.put("token", token);
            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.badRequest().body(new ErrorResponse(response));
        }
    }
}
