package com.Mahmoud.sales_backend.module.Auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.Mahmoud.sales_backend.model.User;
import com.Mahmoud.sales_backend.security.PasswordUtil;
import com.Mahmoud.sales_backend.shared.ApiResponse;
import com.Mahmoud.sales_backend.shared.PhoneValidator;

@Service
public class AuthService {

    private final AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    // ================= LOGIN =================
    public ApiResponse login(String email, String password) {
        if (email == null || email.isEmpty())
            return ApiResponse.error("Email is required");
        if (password == null || password.isEmpty())
            return ApiResponse.error("Password is required");

        User user = authRepository.findByEmail(email.trim());

        if (user == null)
            return ApiResponse.error("User not found");

        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash()))
            return ApiResponse.error("Invalid password");

        String token = authRepository.generateToken(user);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", user.getId());
        data.put("name", user.getName());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("governorate", user.getGovernorate());
        data.put("role", user.getRole());

        return ApiResponse.success(data);
    }

    // ================= REGISTER =================
    public ApiResponse register(User user) {
        if (user.getName() == null || user.getName().isBlank())
            return ApiResponse.error("Name is required");
        if (user.getEmail() == null || user.getEmail().isBlank())
            return ApiResponse.error("Email is required");
        if (user.getPassword() == null || user.getPassword().length() < 6)
            return ApiResponse.error("Password must be at least 6 characters");
        if (user.getPhone() != null && !user.getPhone().isBlank()
                && !PhoneValidator.isValidEgyptPhone(user.getPhone()))
            return ApiResponse.error("Invalid phone number");

        User existing = authRepository.findByEmail(user.getEmail().trim());
        if (existing != null)
            return ApiResponse.error("Email already registered");

        User created = authRepository.create(user);
        if (created == null)
            return ApiResponse.error("Failed to create account");

        Map<String, Object> data = new HashMap<>();
        data.put("id", created.getId());
        data.put("name", created.getName());
        data.put("email", created.getEmail());
        data.put("role", created.getRole());

        return ApiResponse.success(data);
    }
}
