package com.youcode.bankify.service;

import com.youcode.bankify.entity.Role;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Constructor to inject UserRepository
    public UserDetailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        // If the user is not found in the database, throw an exception
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        User user = userOptional.get();

        // Creating a UserBuilder instance from Spring Security User class
        UserBuilder builder = org.springframework.security.core.userdetails.User.withUsername(user.getUsername());
        builder.password(user.getPassword()); // Set the hashed password
        builder.roles(user.getRoles().stream().map(Role::getName).toArray(String[]::new)); // Set roles
        builder.disabled(!user.isEnabled()); // Set whether the user is enabled or not

        // Build and return a UserDetails object that Spring Security will use
        return builder.build();
    }
}
