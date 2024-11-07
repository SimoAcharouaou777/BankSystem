package com.youcode.bankify.service;


import com.youcode.bankify.dto.RegisterRequest;
import com.youcode.bankify.entity.Role;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.RoleRepository;
import com.youcode.bankify.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;


    @Autowired
    public AuthService(UserRepository userRepository, RoleRepository roleRepository){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public String register(RegisterRequest registerRequest){
        if(userRepository.findByUsername(registerRequest.getUsername()).isPresent()){
            return "Username is already exists";
        }
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(BCrypt.hashpw(registerRequest.getPassword(), BCrypt.gensalt()));
        user.setEnabled(true);

        Role userRole = roleRepository.findByName("USER")
                        .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Collections.singleton(userRole));
        userRepository.save(user);
        return "user registered sucessfully";
    }

    public String authenticate(String username, String password){
        Optional<User> userOpt = userRepository.findByUsername(username);
        if(userOpt.isPresent()){
            User user = userOpt.get();
            if(BCrypt.checkpw(password, user.getPassword())){
                return "Authentication successful";
            }else{
                return "Invalid password";
            }
        }else{
            return "user not found";
        }
    }

    public Optional<User> getUserByUsername(String username){
        return userRepository.findByUsername(username);
    }
}
