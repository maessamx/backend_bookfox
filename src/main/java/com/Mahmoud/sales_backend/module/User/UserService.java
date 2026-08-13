package com.Mahmoud.sales_backend.module.User;

import java.util.List;

import org.springframework.stereotype.Service;

import com.Mahmoud.sales_backend.model.User;
import com.Mahmoud.sales_backend.security.UserContext;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ================= GET ALL (admin) =================
    public ApiResponse getAll() {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");

        List<User> users = userRepository.getAll();
        return ApiResponse.success(users);
    }

    // ================= GET BY ID =================
    public ApiResponse getById(int id) {
        if (id <= 0)
            return ApiResponse.error("Invalid ID");

        User user = userRepository.getById(id);
        if (user == null)
            return ApiResponse.error("User not found");

        return ApiResponse.success(user);
    }

    // ================= GET COUNT =================
    public ApiResponse getCount() {
        return ApiResponse.success(userRepository.getCount());
    }

    // ================= GET MY PROFILE =================
    public ApiResponse getMyProfile() {
        Integer userId = UserContext.getUserId();
        if (userId == null)
            return ApiResponse.error("Not authenticated");

        User user = userRepository.getById(userId);
        if (user == null)
            return ApiResponse.error("User not found");

        return ApiResponse.success(user);
    }

    // ================= CREATE =================
    public ApiResponse create(User user) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (user.getName() == null || user.getName().isBlank())
            return ApiResponse.error("Name is required");
        if (user.getEmail() == null || user.getEmail().isBlank())
            return ApiResponse.error("Email is required");
        if (user.getPassword() == null || user.getPassword().length() < 6)
            return ApiResponse.error("Password must be at least 6 characters");

        if (userRepository.findByEmail(user.getEmail().trim()) != null)
            return ApiResponse.error("Email already exists");

        boolean created = userRepository.create(user);
        if (created)
            return ApiResponse.success("User created successfully");

        return ApiResponse.error("Failed to create user");
    }

    // ================= UPDATE =================
    public ApiResponse update(User user) {
        if (user.getId() <= 0)
            return ApiResponse.error("Invalid ID");
        if (user.getName() == null || user.getName().isBlank())
            return ApiResponse.error("Name is required");

        User existing = userRepository.getById(user.getId());
        if (existing == null)
            return ApiResponse.error("User not found");

        boolean updated = userRepository.update(user);
        if (updated)
            return ApiResponse.success("User updated successfully");

        return ApiResponse.error("Failed to update user");
    }

    // ================= CHANGE ROLE (admin) =================
    public ApiResponse changeRole(int id, String role) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (id <= 0)
            return ApiResponse.error("Invalid ID");
        if (role == null || !(role.equals("admin") || role.equals("user")))
            return ApiResponse.error("Invalid role");

        User existing = userRepository.getById(id);
        if (existing == null)
            return ApiResponse.error("User not found");

        boolean updated = userRepository.changeRole(id, role);
        if (updated)
            return ApiResponse.success("Role updated successfully");

        return ApiResponse.error("Failed to update role");
    }

    // ================= DELETE =================
    public ApiResponse delete(int id) {
        if (!isAdmin())
            return ApiResponse.error("Not allowed");
        if (id <= 0)
            return ApiResponse.error("Invalid ID");

        boolean deleted = userRepository.delete(id);
        if (deleted)
            return ApiResponse.success("User deleted successfully");

        return ApiResponse.error("Delete failed");
    }

    private boolean isAdmin() {
        return "admin".equals(UserContext.getRole());
    }
}
