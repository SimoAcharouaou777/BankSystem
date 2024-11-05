package com.youcode.bankify.repository;

import com.youcode.bankify.entity.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<BankAccount,Long> {
    Page<BankAccount> findByUserId(Long userId, Pageable pageable);
    boolean existsByAccountNumber(String accountNumber);
}
