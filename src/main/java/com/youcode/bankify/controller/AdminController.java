package com.youcode.bankify.controller;


import com.youcode.bankify.dto.RegisterRequest;
import com.youcode.bankify.entity.BankAccount;
import com.youcode.bankify.entity.User;
import com.youcode.bankify.service.AdminService;
import com.youcode.bankify.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;

    private void checkAdminRole(HttpSession session){
        Set<String> roles = (Set<String>) session.getAttribute("roles");
        if(roles == null ){
            System.out.println("Roles are null in session , access denied");
            throw new RuntimeException("Access denied ");
        } else if (!roles.contains("ADMIN")) {
            System.out.println("User does not have ADMIN role , roles present : "+roles);
            throw new RuntimeException("Access denied");
        }
        System.out.println("Access granted. Roles: " +roles);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(HttpSession session, @RequestBody RegisterRequest registerRequest){
        checkAdminRole(session);
        User createdUser = adminService.createUser(registerRequest);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(HttpSession session, @PathVariable Long id , @RequestBody User user){
        checkAdminRole(session);
        User updatedUser = adminService.updateUser(id,user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser( HttpSession session, @PathVariable Long id){
        checkAdminRole(session);
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(HttpSession session){
        checkAdminRole(session);
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/accounts/{accountId}/status")
    public ResponseEntity<BankAccount> updateAccountStatus(HttpSession session, @PathVariable Long accountId, @RequestParam String status){
        checkAdminRole(session);
        BankAccount updatedAccount = adminService.updateAccountStatus(accountId, status);
        return ResponseEntity.ok(updatedAccount);
    }
    @GetMapping("/accounts")
    public ResponseEntity<List<BankAccount>> getAllBankAccounts(HttpSession session){
        checkAdminRole(session);
        List<BankAccount> bankAccounts = adminService.getAllBankAccounts();
        return ResponseEntity.ok(bankAccounts);
    }




}
