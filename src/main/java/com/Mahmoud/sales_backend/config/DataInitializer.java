package com.Mahmoud.sales_backend.config;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.Mahmoud.sales_backend.Repository.BaseRepository;
import com.Mahmoud.sales_backend.security.PasswordUtil;

@Component
public class DataInitializer extends BaseRepository implements ApplicationRunner {

    public DataInitializer(Connection conn) {
        super(conn);
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            ensureTable();
            ensureAccount("admin@gmail.com", "admin123", "Mahmoud Essam", "01000000000", "Cairo", "admin");
            ensureAccount("user@gmail.com", "user123", "Youssef Farag", "01111111111", "Giza", "user");
            System.out.println("DataInitializer: default accounts are ready");
        } catch (Exception e) {
            System.err.println("DataInitializer skipped: " + e.getMessage());
        }
    }

    private void ensureTable() {
        String ddl = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(150) UNIQUE,
                phone VARCHAR(20),
                governorate VARCHAR(100),
                password VARCHAR(255) NOT NULL,
                token VARCHAR(255),
                role ENUM('admin','user') DEFAULT 'user',
                status TINYINT(1) DEFAULT 1,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
            """;
        execute(ddl);
    }

    private void ensureAccount(String email, String rawPassword, String name, String phone, String gov, String role) {
        List<Map<String, Object>> result = query(
                "SELECT COUNT(*) AS cnt FROM users WHERE email = ?", email);

        boolean exists = false;
        if (!result.isEmpty() && result.get(0).get("cnt") != null) {
            exists = ((Number) result.get(0).get("cnt")).intValue() > 0;
        }

        if (exists) {
            return;
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("phone", phone);
        data.put("governorate", gov);
        data.put("password", PasswordUtil.hashPassword(rawPassword));
        data.put("role", role);

        int rows = insert("users", data);
        if (rows > 0) {
            System.out.println("DataInitializer: created " + role + " account " + email);
        }
    }
}
