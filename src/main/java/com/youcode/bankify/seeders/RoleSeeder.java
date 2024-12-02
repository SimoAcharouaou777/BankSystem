package com.youcode.bankify.seeders;


import com.youcode.bankify.entity.Role;
import com.youcode.bankify.repository.jpa.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Order(1)
public class RoleSeeder implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception{
        seedRoles();
    }

    private void seedRoles(){
        if(roleRepository.count() == 0){
            Role userRole = new Role();
            userRole.setName("USER");
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            Role employeeRole = new Role();
            employeeRole.setName("EMPLOYEE");
            roleRepository.saveAll(Arrays.asList(userRole,adminRole,employeeRole));
            System.out.println("Roles seeded : USER, ADMIN , EMPLOYEE");
        }else {
            System.out.println("Roles already exist , skipping seeding .");
        }
    }
}
