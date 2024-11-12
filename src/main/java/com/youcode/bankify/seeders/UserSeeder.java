package com.youcode.bankify.seeders;

import com.youcode.bankify.entity.Role;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.jpa.RoleRepository;
import com.youcode.bankify.repository.jpa.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("Role not found"));
            Role userRole = roleRepository.findByName("USER").orElseThrow(() -> new RuntimeException("Role not found"));
            Role employeeRole = roleRepository.findByName("EMPLOYEE").orElseThrow(() -> new RuntimeException("Role not found"));

            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(BCrypt.hashpw("admin", BCrypt.gensalt()));
            adminUser.setFirstName("admin");
            adminUser.setLastName("admin");
            adminUser.setDateOfBirth(LocalDate.of(1980, 1, 1));
            adminUser.setIdentityNumber("111111111");
            adminUser.setAge(calculateAge(adminUser.getDateOfBirth()));
            adminUser.setEnabled(true);
            adminUser.setRoles(new HashSet<>(Set.of(adminRole)));

            User normalUser = new User();
            normalUser.setUsername("user");
            normalUser.setPassword(BCrypt.hashpw("user", BCrypt.gensalt()));
            normalUser.setFirstName("user");
            normalUser.setLastName("User");
            normalUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
            normalUser.setIdentityNumber("222222222");
            normalUser.setAge(calculateAge(adminUser.getDateOfBirth()));
            normalUser.setEnabled(true);
            normalUser.setRoles(new HashSet<>(Set.of(userRole)));

            User employeeUser = new User();
            employeeUser.setUsername("employee");
            employeeUser.setPassword(BCrypt.hashpw("employee", BCrypt.gensalt()));
            employeeUser.setFirstName("Employee");
            employeeUser.setLastName("employee");
            employeeUser.setDateOfBirth(LocalDate.of(1985, 1, 1));
            employeeUser.setIdentityNumber("333333333");
            employeeUser.setAge(calculateAge(employeeUser.getDateOfBirth()));
            employeeUser.setEnabled(true);
            employeeUser.setRoles(new HashSet<>(Set.of(employeeRole)));

            userRepository.saveAll(List.of(adminUser, normalUser, employeeUser));
            System.out.println("Users seeded: admin, user, employee");
        } else {
            System.out.println("Users already exist, skipping seeding.");
        }
    }

    private int calculateAge(LocalDate birthDate){
        if(birthDate == null){
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
}