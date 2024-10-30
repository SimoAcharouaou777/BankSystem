package com.youcode.bankify.service;


import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;


    @Autowired
    public AuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public String register(User user){
        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            return "Username is already existe";
        }
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setEnabled(true);
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
}
