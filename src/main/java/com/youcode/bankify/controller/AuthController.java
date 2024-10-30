package com.youcode.bankify.controller;


import com.youcode.bankify.dto.LoginRequest;
import com.youcode.bankify.dto.RegisterRequest;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.UserRepository;
import com.youcode.bankify.service.AuthService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest registerRequest){
        String response = authService.register(registerRequest);
        if(response.equals("User registered successfully")){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequest loginRequest){
        String response = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        if(response.equals("Authentication successful")){
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }


}
