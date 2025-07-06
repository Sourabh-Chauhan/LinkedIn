package com.linkedin.linkedin.features.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestBody(
                @NotBlank(message = "Email is mandatory") @Email(message = "Invalid email format") String email,
                @NotBlank(message = "Password is mandatory") String password) {
}