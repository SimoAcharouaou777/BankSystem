package com.youcode.bankify.controller;


import com.youcode.bankify.dto.ErrorResponse;
import com.youcode.bankify.dto.LoginRequest;
import com.youcode.bankify.dto.RegisterRequest;
import com.youcode.bankify.entity.Role;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest){
        String response = authService.register(registerRequest);
        Map<String , String > responseBody = new HashMap<>();

        if(response.equals("user registered sucessfully")){
            responseBody.put("message" , "User registered successfully");
            return ResponseEntity.ok(responseBody);
        }else{
            responseBody.put("message", response);
            return ResponseEntity.badRequest().body(responseBody);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest, HttpSession session){

        String response = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if(response.equals("Authentication successful")){
            Optional<User> optionalUser = authService.getUserByUsername(loginRequest.getUsername());
            if(optionalUser.isEmpty()){
                return ResponseEntity.status(401).body(new ErrorResponse("User not found"));
            }
            User user = optionalUser.get();

            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

            Map<String , Object> responseBody = new HashMap<>();
            responseBody.put("message", "user logged in successfully");
            responseBody.put("sessionId", session.getId());
            responseBody.put("username", session.getAttribute("username"));
            return ResponseEntity.ok(responseBody);
        } else {
            return ResponseEntity.badRequest().body(new ErrorResponse(response));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session){
        if(session == null || session.getAttribute("userId") == null){
            return ResponseEntity.status(401).body("No user is curently logged in");
        }
        Long userId = (Long) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        session.invalidate();
        return ResponseEntity.ok(" user" +username+" (ID : "+userId+ ") logged out successfulluy");
    }


}
