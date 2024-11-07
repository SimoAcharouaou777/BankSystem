package com.youcode.bankify.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AccountCreationDTO {
    private String firstName;
    private String lastName;
    private String identityNumber;
    private LocalDate dateOfBirth;
}
