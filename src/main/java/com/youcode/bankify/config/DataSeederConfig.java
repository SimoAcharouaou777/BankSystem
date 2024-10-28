package com.youcode.bankify.config;

import com.youcode.bankify.entity.Role;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.RoleRepository;
import com.youcode.bankify.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Configuration
@Profile("seed")  // Activate this class only with 'seed' profile
public class DataSeederConfig {

    @Bean
    CommandLineRunner seedDatabase(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) {
                // Check if roles already exist
                if (roleRepository.count() == 0) {
                    Role userRole = new Role();
                    userRole.setName("USER");
                    Role adminRole = new Role();
                    adminRole.setName("ADMIN");
                    Role employeeRole = new Role();
                    employeeRole.setName("EMPLOYEE");

                    roleRepository.saveAll(List.of(userRole, adminRole, employeeRole));
                }

                if (userRepository.count() == 0) {
                    List<Role> roles = roleRepository.findAll();
                    Random random = new Random();

                    for (int i = 0; i < 1000; i++) {
                        User user = new User();
                        user.setUsername("user_" + i);
                        user.setPassword(passwordEncoder.encode("password"));  // Encrypt the password
                        user.setEnabled(true);

                        Set<Role> userRoles = new HashSet<>();
                        userRoles.add(roles.get(random.nextInt(roles.size())));
                        user.setRoles(userRoles);

                        userRepository.save(user);
                    }
                }
            }
        };
    }
}
