package com.youcode.bankify.seeders;

import com.github.javafaker.Faker;
import com.youcode.bankify.entity.Role;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.RoleRepository;
import com.youcode.bankify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;

//@Component
//public class UserSeeder implements CommandLineRunner {

//    @Autowired
//    RoleRepository roleRepository;
//
//    @Autowired
//    UserRepository userRepository;
//
//    private final Faker faker = new Faker();
//    private final Random random = new Random();
//
//    @Override
//    public void run(String... args) throws Exception {
//        seedUsers();
//    }
//
//    private void seedUsers(){
//        int userCount = 12000000;
//        int batchSize = 3000;
//
//        List<User> userToSave = new ArrayList<>();
//        List<Role> roles = roleRepository.findAll();
//        Set<String> generatedUsernames = new HashSet<>();
//
//        for (int i=0; i<userCount; i++){
//            User user = new User();
//            String username;
//            do{
//                username = faker.name().username();
//            }while (generatedUsernames.contains(username) || userRepository.existsByUsername(username));
//            generatedUsernames.add(username);
//
//            user.setUsername(username);
//            user.setPassword(faker.internet().password());
//            user.setEnabled(true);
//            Role role = roles.get(random.nextInt(roles.size()));
//            user.setRoles(Collections.singleton(role));
//            userToSave.add(user);
//
//            if(userToSave.size() == batchSize){
//                userRepository.saveAll(userToSave);
//                userToSave.clear();
//                System.out.println("saved "+(i+1)+" users");
//            }
//        }
//        if(!userToSave.isEmpty()){
//            userRepository.saveAll(userToSave);
//            System.out.println("saved remaining users");
//        }
//        System.out.println("user seeding completed");
//    }
//}
