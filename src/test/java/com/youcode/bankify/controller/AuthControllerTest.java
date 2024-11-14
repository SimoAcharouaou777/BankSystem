package com.youcode.bankify.controller;

import com.youcode.bankify.dto.LoginRequest;
import com.youcode.bankify.dto.RegisterRequest;
import com.youcode.bankify.entity.Role;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthController authController;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEnabled(true);

        role = new Role();
        role.setName("USER");
        user.setRoles(Set.of(role));

    }

    @Test
    public void registerUserSuccess() {
        when(authService.register(any(RegisterRequest.class))).thenReturn("user registered sucessfully");

        ResponseEntity<?> response = authController.registerUser(registerRequest);
        assertEquals(200, response.getStatusCode().value());
    }

     @Test
     public void testRegisterUserFailure(){
        when(authService.register(any(RegisterRequest.class))).thenReturn("Username is already exists");

        ResponseEntity<?> response = authController.registerUser(registerRequest);
        assertEquals(400, response.getStatusCode().value());
     }

    @Test
    public void loginUser() {
        when(authService.authenticate(any(String.class), any(String.class))).thenReturn("Authentication successful");
        when(authService.getUserByUsername(any(String.class))).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.loginUser(loginRequest, session);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void loginUserFailure(){
        when(authService.authenticate(any(String.class), any(String.class))).thenReturn("Invalid password");

        ResponseEntity<?> response = authController.loginUser(loginRequest, session);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    public void  logoutSuccess() {
        when(session.getAttribute("userId")).thenReturn(1L);
        when(session.getAttribute("username")).thenReturn("testuser");

        ResponseEntity<String> response = authController.logout(session);
        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void logoutFailure(){
        when(session.getAttribute("userId")).thenReturn(null);

        ResponseEntity<String> response = authController.logout(session);
        assertEquals(401, response.getStatusCode().value());
    }
}