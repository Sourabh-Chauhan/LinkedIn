package com.linkedin.backend.features.authentication.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.linkedin.backend.features.feed.model.Post;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @PrePersist
    @PreUpdate
    void updateProfileComplete() {
        System.out.println("Updating profileComplete for user: " + this.id);
        Boolean profileCompleteBool = (this.firstName != null && !this.firstName.isEmpty())
                && (this.lastName != null && !this.lastName.isEmpty())
                && (this.company != null && !this.company.isEmpty())
                && (this.position != null && !this.position.isEmpty())
                && (this.location != null && !this.location.isEmpty())
                // && (this.profilePicture != null && !this.profilePicture.isEmpty())
                // && (this.coverPicture != null && !this.coverPicture.isEmpty())
                && (this.about != null && !this.about.isEmpty());

        System.out.println("Profile is complete." + (this.lastName != null && !this.lastName.isEmpty())
                + (profileComplete ? " Setting profileComplete to true." : " Setting profileComplete to false."));
        this.setProfileComplete(profileCompleteBool);
    }
}