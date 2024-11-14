package com.youcode.bankify.service;

import com.youcode.bankify.dto.RegisterRequest;
import com.youcode.bankify.entity.Role;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.jpa.RoleRepository;
import com.youcode.bankify.repository.jpa.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User user ;
    private Role role;

    @BeforeEach
    public void setUp(){
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password");

        user = new User();
        user.setUsername("testuser");
        user.setPassword(BCrypt.hashpw("password" ,BCrypt.gensalt()));
        user.setEnabled(true);

        role = new Role();
        role.setName("USER");
    }

    @Test
    public void  testRegister() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());
        when(roleRepository.findByName(any(String.class))).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        String response = authService.register(registerRequest);
        assertEquals("user registered sucessfully", response);
    }

    @Test
    public void testRegisterUserAlreadyExists(){
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        String response = authService.register(registerRequest);
        assertEquals("Username is already exists", response);
    }

    @Test
    public void authenticate() {
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        String response = authService.authenticate("testuser","password");
        assertEquals("Authentication successful" , response);
    }

     @Test
     public void authenticateWithWrongPassword(){
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));

        String response = authService.authenticate("testuser","ostohohiz");
        assertEquals("Invalid password" , response);
     }

     @Test
     public void authenticateWithWrongUserName(){
        when(userRepository.findByUsername("nonExistUsers")).thenReturn(Optional.empty());

        String response = authService.authenticate("nonExistUsers","password");
        assertEquals("user not found", response);
     }

}