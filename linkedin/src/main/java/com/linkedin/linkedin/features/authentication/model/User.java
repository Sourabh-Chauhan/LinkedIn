package com.linkedin.linkedin.features.authentication.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
// import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
// import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Data
@Entity
@Table(name = "user")
@NoArgsConstructor

public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Email
    @Column(unique = true)
    private String email;
    private Boolean emailVerified = false;
    private String emailVerificationToken = null;
    private LocalDateTime emailVerificationTokenExpiryDate = null;
    @JsonIgnore
    private String password;
    private String passwordResetToken = null;
    private LocalDateTime passwordResetTokenExpiryDate = null;

    // @FullTextField(analyzer = "standard")
    private String firstName = null;
    // @FullTextField(analyzer = "standard")
    private String lastName = null;
    // @FullTextField(analyzer = "standard")
    private String company = null;
    // @FullTextField(analyzer = "standard")
    private String position = null;
    private String location = null;
    private String profilePicture = null;
    private String coverPicture = null;
    private Boolean profileComplete = false;
    private String about = null;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}