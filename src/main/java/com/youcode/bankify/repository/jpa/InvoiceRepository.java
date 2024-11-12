package com.youcode.bankify.repository.jpa;

import com.youcode.bankify.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    List<Invoice> findByUserId(Long userId);
}
