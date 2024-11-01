package com.youcode.bankify.controller;


import com.youcode.bankify.dto.ErrorResponse;
import com.youcode.bankify.dto.LoginRequest;
import com.youcode.bankify.dto.RegisterRequest;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.UserRepository;
import com.youcode.bankify.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest){
        String response = authService.register(registerRequest);
        if(response.equals("user registered sucessfully")){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(new ErrorResponse(response));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpSession session){
        String response = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if(response.equals("Authentication successful")){
            session.setAttribute("username", loginRequest.getUsername());
            Map<String , Object> responseBody = new HashMap<>();
            responseBody.put("message", "user logged in successfully");
            responseBody.put("sessionId", session.getId());
            responseBody.put("username", session.getAttribute("username"));
            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.badRequest().body(new ErrorResponse(response));
        }
    }


}
