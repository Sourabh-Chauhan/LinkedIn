package com.linkedin.linkedin.features.authentication.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class Encoder {
    public String encode(String rawString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(rawString.getBytes());
            return Base64.getEncoder().encodeToString(encodedhash);

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException("Error encoding string", e);
        }

    }

    public boolean matches(String rawString, String encodedString) {
        // Compare the raw string with the encoded string using the same encoding method
        // and return true if they match, false otherwise.
        // Note: This is a simple comparison and may not be secure for all use cases.
        return encode(rawString).equals(encodedString);
    }
}
