package com.linkedin.linkedin.features.authentication.service;

import com.linkedin.linkedin.features.authentication.dto.AuthenticationRequestBody;
import com.linkedin.linkedin.features.authentication.dto.AuthenticationResponseBody;
import com.linkedin.linkedin.features.authentication.model.User;

public interface AuthenticationService {

    AuthenticationResponseBody login(AuthenticationRequestBody loginRequestBody);

    AuthenticationResponseBody register(AuthenticationRequestBody registerRequestBody);

    AuthenticationResponseBody googleLoginOrSignup(String code, String page);

    void sendPasswordResetToken(String email);

    void resetPassword(String email, String newPassword, String token);

    User updateUserProfile(User user, Long id, String firstName, String lastName, String company, String position,
            String location,
            String profilePicture, String coverPicture, String about);

    User getUserById(Long id);

    User getUser(String email);

    void validateEmailVerificationToken(String token, String email);

    void sendEmailVerificationToken(String email);

    void deleteUser(Long id);

}
