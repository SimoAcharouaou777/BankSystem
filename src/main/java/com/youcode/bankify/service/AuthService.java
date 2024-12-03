package com.youcode.bankify.service;


import com.youcode.bankify.dto.RegisterRequest;
import com.youcode.bankify.entity.Role;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.jpa.RoleRepository;
import com.youcode.bankify.repository.jpa.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService implements UserDetailsService {

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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(Role::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
