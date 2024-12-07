package com.youcode.bankify.service;


import com.youcode.bankify.dto.RegisterRequest;
import com.youcode.bankify.dto.UpdatedUserRequest;
import com.youcode.bankify.entity.BankAccount;
import com.youcode.bankify.entity.Role;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.repository.jpa.AccountRepository;
import com.youcode.bankify.repository.jpa.RoleRepository;
import com.youcode.bankify.repository.jpa.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(RegisterRequest registerRequest){
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(true);
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setDateOfBirth(registerRequest.getDateOfBirth());
        user.setIdentityNumber(registerRequest.getIdentityNumber());

        LocalDate birthDate = registerRequest.getDateOfBirth();
        if(birthDate != null){
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            user.setAge(age);
        }

        Set<Role> roles = registerRequest.getRoles().stream()
                .map(roleName -> roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("Role not found ")))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public User updateUser(Long userId, UpdatedUserRequest userDetails){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setUsername(userDetails.getUsername());
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setDateOfBirth(userDetails.getDateOfBirth());
        user.setIdentityNumber(userDetails.getIdentityNumber());
        user.setEnabled(true);

        if(userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()){
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        Set<Role> roles = userDetails.getRoles().stream()
                        .map(roleName -> roleRepository.findByName(roleName)
                                .orElseThrow(() -> new RuntimeException("Role not found" + roleName)))
                                .collect(Collectors.toSet());
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public ResponseEntity<String > deleteUser(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
        return ResponseEntity.ok("User deleted successfullu");
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public BankAccount updateAccountStatus(Long accountId , String status){
        BankAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        account.setStatus(status.toUpperCase());
        return accountRepository.save(account);
    }

    public List<BankAccount> getAllBankAccounts(){return accountRepository.findAll();}


}
