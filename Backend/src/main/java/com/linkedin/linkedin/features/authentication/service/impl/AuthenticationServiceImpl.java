package com.linkedin.linkedin.features.authentication.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.linkedin.linkedin.features.authentication.dto.AuthenticationRequestBody;
import com.linkedin.linkedin.features.authentication.dto.AuthenticationResponseBody;
import com.linkedin.linkedin.features.authentication.model.User;
import com.linkedin.linkedin.features.authentication.repository.UserRepository;
import com.linkedin.linkedin.features.authentication.service.AuthenticationService;
import com.linkedin.linkedin.features.authentication.utils.EmailService;
import com.linkedin.linkedin.features.authentication.utils.Encoder;
import com.linkedin.linkedin.features.authentication.utils.JsonWebToken;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
// @Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final UserRepository userRepository;
    private final int durationInMinutes = 1;

    private final Encoder encoder;
    private final JsonWebToken jsonWebToken;
    private final EmailService emailService;
    private final RestTemplate restTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public AuthenticationServiceImpl(UserRepository userRepository, Encoder encoder, JsonWebToken jsonWebToken,
            EmailService emailService, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jsonWebToken = jsonWebToken;
        this.emailService = emailService;
        this.restTemplate = restTemplate;
    }

    @Override
    public AuthenticationResponseBody login(AuthenticationRequestBody loginRequestBody) {

        User user = userRepository.findByEmail(loginRequestBody.email()).orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("User with email %s not found", loginRequestBody.email())));

        if (!encoder.matches(loginRequestBody.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jsonWebToken.generateToken(loginRequestBody.email());
        return new AuthenticationResponseBody(token, "User logged in successfully");
    }

    @Transactional
    @Override
    public AuthenticationResponseBody register(AuthenticationRequestBody registerRequestBody) {
        User user = userRepository.save(new User(registerRequestBody.email(),
                encoder.encode(registerRequestBody.password())));

        String emailVerificationToken = generateEmailVerificationToken();
        String hashedToken = encoder.encode(emailVerificationToken);
        user.setEmailVerificationToken(hashedToken);
        user.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));
        userRepository.save(user);

        String subject = "Email Verification";
        String body = String.format("""
                Only one step to take full advantage of LinkedIn.
                /n

                Enter this code to verify your email: %s. The code will expire in %s minutes.""",
                emailVerificationToken, durationInMinutes);

        try {
            emailService.sendEmail(registerRequestBody.email(), subject, body);
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.info("Error while sending email: {}", e.getMessage());
            throw new RuntimeException("Error while sending email verification token", e);
        } catch (Exception e) {
            logger.info("Unknown Error while sending email: {}", e.getMessage());
            throw new RuntimeException("Unknown while sending email verification token", e);
        }

        String authToken = jsonWebToken.generateToken(registerRequestBody.email());
        return new AuthenticationResponseBody(authToken, "User registered successfully.");

        // throw new UnsupportedOperationException("Unimplemented method 'register' " +
        // token);

    }

    @Override
    public AuthenticationResponseBody googleLoginOrSignup(String code, String page) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'googleLoginOrSignup'");
    }

    @Override
    public void sendPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("User with email %s not found", email)));
        if (user.getPasswordResetToken() != null && user.getPasswordResetTokenExpiryDate() != null
                && user.getPasswordResetTokenExpiryDate().isAfter(LocalDateTime.now().minusSeconds(30))) {
            throw new IllegalArgumentException("Password reset token already sent.");
        }
        String passwordResetToken = generateEmailVerificationToken();
        String hashedToken = encoder.encode(passwordResetToken);
        user.setPasswordResetToken(hashedToken);
        user.setPasswordResetTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));
        userRepository.save(user);
        String subject = "Password Reset";
        String body = String.format("""
                You requested a password reset.

                Enter this code to reset your password: %s. The code will expire in %s minutes.""",
                passwordResetToken, durationInMinutes);

        try {
            emailService.sendEmail(email, subject, body);
        } catch (Exception e) {
            logger.info("Error while sending email: {}", e.getMessage());
            throw new RuntimeException("Error while sending password reset token", e);
        }

    }

    @Override
    public void resetPassword(String email, String newPassword, String token) {

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("User with email %s not found", email)));
        if (user.getPasswordResetToken() == null) {
            throw new IllegalArgumentException("Password reset token not found.");
        }
        if (user.getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalArgumentException("Password reset token expired.");
        }
        if (!encoder.matches(token, user.getPasswordResetToken())) {
            throw new IllegalArgumentException("Invalid password reset token.");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiryDate(null);
        userRepository.save(user);

    }

    @Override
    @Transactional
    public User updateUserProfile(User user, Long id, String firstName, String lastName, String company,
            String position,
            String location, String profilePicture, String coverPicture, String about) {
        if (firstName != null)
            user.setFirstName(firstName);
        if (lastName != null)
            user.setLastName(lastName);
        if (company != null)
            user.setCompany(company);
        if (position != null)
            user.setPosition(position);
        if (location != null)
            user.setLocation(location);
        if (about != null)
            user.setAbout(about);

        return userRepository.save(user);

    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    @Transactional(readOnly = true)
    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    @Override
    public void validateEmailVerificationToken(String token, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("User with email %s not found", email)));
        if (user.getEmailVerificationToken() == null) {
            throw new IllegalArgumentException("Email verification token not found.");
        }
        if (user.getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new IllegalArgumentException("Email verification token expired.");
        }
        if (!encoder.matches(token, user.getEmailVerificationToken())) {
            throw new IllegalArgumentException("Invalid email verification token.");
        }
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiryDate(null);
        userRepository.save(user);

    }

    @Override
    public void sendEmailVerificationToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new IllegalArgumentException(
                        String.format("User with email %s not found", email)));
        if (user.getEmailVerified()) {
            throw new IllegalArgumentException("Email already verified.");
        }
        if (user.getEmailVerificationToken() != null && user.getEmailVerificationTokenExpiryDate() != null
                && user.getEmailVerificationTokenExpiryDate().isAfter(LocalDateTime.now().minusSeconds(30))) {
            throw new IllegalArgumentException("Email verification token already sent.");
        }

        String emailVerificationToken = generateEmailVerificationToken();
        String hashedToken = encoder.encode(emailVerificationToken);
        user.setEmailVerificationToken(hashedToken);
        user.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));
        userRepository.save(user);

        String subject = "Email Verification";
        String body = String.format("""
                Only one step to take full advantage of LinkedIn.

                Enter this code to verify your email: %s

                The code will expire in %s minutes.""",
                emailVerificationToken, durationInMinutes);

        try {
            emailService.sendEmail(email, subject, body);
        } catch (Exception e) {
            logger.info("Error while sending email: {}", e.getMessage());
            throw new RuntimeException("Error while sending email verification token", e);
        }

    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        // User user = userRepository.findById(userId).orElseThrow(
        // () -> new IllegalArgumentException(
        // String.format("User with id %s not found", userId)));

        User user = entityManager.find(User.class, userId);
        if (user != null) {
            entityManager.createNativeQuery("DELETE FROM posts_likes WHERE user_id = :userId")
                    .setParameter("userId", userId)
                    .executeUpdate();
            entityManager.remove(user);
        }
    }

    public static String generateEmailVerificationToken() {
        // return UUID.randomUUID().toString();
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(random.nextInt(10));
        }
        // System.out.println("qazwsx : " + sb);
        return sb.toString();
    }

}
