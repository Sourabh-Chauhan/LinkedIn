package com.linkedin.linkedin.features.authentication.filter;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.linkedin.linkedin.features.authentication.model.User;
import com.linkedin.linkedin.features.authentication.service.AuthenticationService;
import com.linkedin.linkedin.features.authentication.utils.JsonWebToken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationFilter extends HttpFilter {

    private final List<String> unsecuredEndpoints = Arrays.asList(
            "/api/v1/authentication/login",
            "/api/v1/authentication/register",
            "/api/v1/authentication/send-password-reset-token",
            "/api/v1/authentication/reset-password");

    private final JsonWebToken jsonWebTokenService;
    private final AuthenticationService authenticationService;

    public AuthenticationFilter(JsonWebToken jsonWebTokenService, AuthenticationService authenticationService) {
        this.jsonWebTokenService = jsonWebTokenService;
        this.authenticationService = authenticationService;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = request.getRequestURI();
        if (unsecuredEndpoints.contains(path) || path.startsWith("/api/v1/authentication/oauth")
                || path.startsWith("/api/v1/storage")) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = request.getHeader("Authorization");
            if (token == null || !token.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid token");
                return;
            }

            token = token.substring(7); // Remove "Bearer " prefix
            if (jsonWebTokenService.isTokenExpired(token)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }

            String email = jsonWebTokenService.getEmailFromToken(token);
            if (email == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
            User user = authenticationService.getUser(email);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }
            request.setAttribute("authenticatedUser", user);
            chain.doFilter(request, response);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");

        }

        // TODO Auto-generated method stub
        // super.doFilter(request, response, chain);
    }

}
