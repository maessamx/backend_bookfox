package com.Mahmoud.sales_backend.module.User;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.Mahmoud.sales_backend.Repository.BaseRepository;
import com.Mahmoud.sales_backend.model.User;
import com.Mahmoud.sales_backend.security.PasswordUtil;

@Repository
public class UserRepository extends BaseRepository {

    public UserRepository(Connection conn) {
        super(conn);
    }

    // ================= GET ALL =================
    public List<User> getAll() {
        List<Map<String, Object>> result = super.getAll("users");
        List<User> users = new ArrayList<>();
        for (Map<String, Object> row : result) {
            users.add(mapToUser(row));
        }
        return users;
    }

    // ================= GET BY ID =================
    public User getById(int id) {
        List<Map<String, Object>> result = query("SELECT * FROM users WHERE id = ?", id);
        if (result.isEmpty())
            return null;
        return mapToUser(result.get(0));
    }

    // ================= FIND BY EMAIL =================
    public User findByEmail(String email) {
        List<Map<String, Object>> result = query("SELECT * FROM users WHERE email = ?", email);
        if (result.isEmpty())
            return null;
        return mapToUser(result.get(0));
    }

    // ================= GET COUNT =================
    public int getCount() {
        return super.count("users");
    }

    // ================= CREATE =================
    public boolean create(User user) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("name", user.getName());
            data.put("email", user.getEmail().trim());
            data.put("phone", user.getPhone());
            data.put("governorate", user.getGovernorate());
            data.put("password", PasswordUtil.hashPassword(user.getPassword()));
            data.put("role", user.getRole() == null ? "user" : user.getRole());

            return insert("users", data) > 0;
        } catch (Exception e) {
            System.out.println("DB ERROR: " + e.getMessage());
            return false;
        }
    }

    // ================= UPDATE =================
    public boolean update(User user) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("name", user.getName());
            data.put("phone", user.getPhone());
            data.put("governorate", user.getGovernorate());

            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                data.put("password", PasswordUtil.hashPassword(user.getPassword()));
            }

            return update("users", data, "id", user.getId()) > 0;
        } catch (Exception e) {
            System.out.println("DB ERROR: " + e.getMessage());
            return false;
        }
    }

    // ================= CHANGE ROLE =================
    public boolean changeRole(int id, String role) {
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("role", role);
            return update("users", data, "id", id) > 0;
        } catch (Exception e) {
            System.out.println("DB ERROR: " + e.getMessage());
            return false;
        }
    }

    // ================= DELETE =================
    public boolean delete(int id) {
        try {
            return delete("users", "id", id) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================= MAPPER =================
    private User mapToUser(Map<String, Object> row) {
        User user = new User();

        user.setId(((Number) row.get("id")).intValue());
        user.setName((String) row.get("name"));
        user.setEmail((String) row.get("email"));
        user.setPhone((String) row.get("phone"));
        user.setGovernorate((String) row.get("governorate"));
        user.setPasswordHash((String) row.get("password"));
        user.setRole((String) row.get("role"));
        user.setStatus(((Number) row.get("status")).intValue());
        user.setCreatedAt((Date) row.get("created_at"));
        user.setUpdatedAt((Date) row.get("updated_at"));

        return user;
    }
}
