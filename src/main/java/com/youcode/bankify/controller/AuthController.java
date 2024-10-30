package com.youcode.bankify.controller;


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
    public ResponseEntity<String> registerUser(@RequestBody User user){
        String response = authService.register(user);
        if(response.equals("User registered successfully")){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestParam String username, @RequestParam String password){
        String response = authService.authenticate(username, password);
        if(response.equals("Authentication successful")){
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }


}
