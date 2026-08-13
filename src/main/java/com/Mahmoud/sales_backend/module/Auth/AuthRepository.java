package com.Mahmoud.sales_backend.module.Auth;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.Mahmoud.sales_backend.Repository.BaseRepository;
import com.Mahmoud.sales_backend.model.User;
import com.Mahmoud.sales_backend.security.PasswordUtil;
import com.Mahmoud.sales_backend.shared.JwtUtil;

@Repository
public class AuthRepository extends BaseRepository {

    private final JwtUtil jwtUtil;

    public AuthRepository(Connection conn, JwtUtil jwtUtil) {
        super(conn);
        this.jwtUtil = jwtUtil;
    }

    // ================= FIND USER BY EMAIL =================
    public User findByEmail(String email) {
        List<Map<String, Object>> result = query("SELECT * FROM users WHERE email = ?", email);

        if (result.isEmpty())
            return null;

        return mapToUser(result.get(0));
    }

    // ================= CREATE USER =================
    public User create(User user) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", user.getName());
        data.put("email", user.getEmail().trim());
        data.put("phone", user.getPhone());
        data.put("governorate", user.getGovernorate());
        data.put("password", PasswordUtil.hashPassword(user.getPassword()));
        data.put("role", "user");

        int rows = insert("users", data);
        if (rows == 0)
            return null;

        return findByEmail(user.getEmail().trim());
    }

    // ================= GENERATE TOKEN =================
    public String generateToken(User user) {
        return jwtUtil.generateToken(user.getId(), user.getRole());
    }

    // ================= MAPPER =================
    private User mapToUser(Map<String, Object> row) {
        User user = new User();

        user.setId(toInt(row.get("id")));
        user.setName((String) row.get("name"));
        user.setEmail((String) row.get("email"));
        user.setPhone((String) row.get("phone"));
        user.setGovernorate((String) row.get("governorate"));
        user.setPasswordHash((String) row.get("password"));
        user.setRole((String) row.get("role"));
        user.setStatus(toInt(row.get("status")));

        return user;
    }
}
