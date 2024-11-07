package com.youcode.bankify.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;
    private Integer termInMonths;
    private String status; // PENDING, APPROVED, REJECTED
    private LocalDateTime applicationDate;

    @ManyToOne
    private User user;
}
