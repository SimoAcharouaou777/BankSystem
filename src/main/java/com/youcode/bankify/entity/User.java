package com.youcode.bankify.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.youcode.bankify.util.serializer.LocalDateDeserializer;
import com.youcode.bankify.util.serializer.LocalDateTimeDeserializer;
import jakarta.persistence.*;
import lombok.Builder;
import com.youcode.bankify.entity.Role;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true , nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled" )
    private boolean enabled = true;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth" , nullable = false)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate dateOfBirth;

    @Column(name = "age" , nullable = false)
    private Integer age;

     @Column(name = "identity_number" , unique = true , nullable = false)
     private String identityNumber;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getFirstName() {return firstName;}

    public Integer getAge() {return age;}

    public LocalDate getDateOfBirth() {return dateOfBirth;}

    public String getLastName() {return lastName;}

    public void setLastName(String lastName) {this.lastName = lastName;}

    public void setFirstName(String firstName) {this.firstName = firstName;}

    public void setAge(Integer age) {this.age = age;}

    public void setDateOfBirth(LocalDate dateOfBirth) {this.dateOfBirth = dateOfBirth;}

    public String getIdentityNumber() {return identityNumber;}

    public void setIdentityNumber(String identityNumber) {this.identityNumber = identityNumber;}
}
