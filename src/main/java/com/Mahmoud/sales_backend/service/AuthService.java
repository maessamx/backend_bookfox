package com.Mahmoud.sales_backend.service;

import org.springframework.stereotype.Service;

import com.Mahmoud.sales_backend.Repository.AuthRepository;
import com.Mahmoud.sales_backend.model.User;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@Service
public class AuthService {

    private final AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    // ================= LOGIN =================
    public ApiResponse login(String email, String password) {
        // ================= EMAIL VALIDATION =================
        if (email == null || email.isEmpty()) {
            return ApiResponse.error("Email is required");
        }

        if (email.length() > 50) {
            return ApiResponse.error("Email must not exceed 50 characters");
        }

        if (!email.endsWith("@gmail.com")) {
            return ApiResponse.error("Email must end with @gmail.com");
        }

        if (email.chars().filter(c -> c == '@').count() != 1) {
            return ApiResponse.error("Email must contain only one @");
        }

        String localPart = email.split("@")[0];

        if (!localPart.matches("[a-z0-9]+")) {
            return ApiResponse.error("Email must contain only lowercase letters and numbers");
        }

        // ================= PASSWORD VALIDATION =================
        if (password == null || password.isEmpty()) {
            return ApiResponse.error("Password is required");
        }
        

        password = password.trim();
        if (!password.matches("^[a-zA-Z0-9]+$")) {
            System.out.println("PASSWORD=[" + password + "]");
            return ApiResponse.error("Password must contain only English letters and numbers");
        }

        User user = authRepository.findByEmail(email);

        if (user == null) {
            return ApiResponse.error("User not found");
        }

        if (!user.getPassword().equals(password)) {
            return ApiResponse.error("Invalid password");
        }

        String token = authRepository.generateToken(user);

        return ApiResponse.success(token);
    }

}