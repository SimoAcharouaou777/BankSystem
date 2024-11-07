package com.youcode.bankify.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;
    private String description;
    private BigDecimal amount;
    private String status; // PENDING, PAID
    private LocalDate dueDate;

    @ManyToOne
    private User user;
}
