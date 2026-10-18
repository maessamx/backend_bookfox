package com.Mahmoud.sales_backend.module.Auth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.Mahmoud.sales_backend.model.User;
import com.Mahmoud.sales_backend.security.PasswordUtil;
import com.Mahmoud.sales_backend.services.EmailService;
import com.Mahmoud.sales_backend.shared.ApiResponse;
import com.Mahmoud.sales_backend.shared.PhoneValidator;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final EmailService emailService;

    public AuthService(AuthRepository authRepository, EmailService emailService) {
        this.authRepository = authRepository;
        this.emailService = emailService;
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

        if(user.getStatus() ==0)
            return ApiResponse.error("account not active");

        String token = authRepository.generateToken(user);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", user.getId());
        data.put("name", user.getName());
        data.put("email", user.getEmail());
        data.put("phone", user.getPhone());
        data.put("governorate", user.getGovernorate());
        data.put("role", user.getRole());
        try {
            String finalBody = "Hello " + user.getName();

            emailService.sendEmail(user.getEmail(), "Order Confirmed", finalBody);
            System.out.println("Email accepted by Gmail for: " + user.getEmail());

        } catch (Exception e) {
            throw new RuntimeException(
                    "Order was saved, but the confirmation email failed: " + e.getMessage(), e);
        }

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

        try {
            String finalBody = "Hello " + user.getName() + " http://localhost:8080/auth/verify-token?token=" + created.getToken();

            emailService.sendEmail(user.getEmail(), "Order Confirmed", finalBody);
            System.out.println("Email accepted by Gmail for: " + user.getEmail());

        } catch (Exception e) {
            throw new RuntimeException(
                    "Order was saved, but the confirmation email failed: " + e.getMessage(), e);
        }

        return ApiResponse.success(data);
    }

    public String verifyToken(String token) {
        boolean result = authRepository.CheckToken(token);

        if (result) {
            return """
                    <!DOCTYPE html>
                    <html lang="ar" dir="rtl">
                    <head>
                        <meta charset="UTF-8">
                        <title>تفعيل الحساب</title>
                        <style>
                            * { margin: 0; padding: 0; box-sizing: border-box; }
                            body {
                                font-family: 'Segoe UI', sans-serif;
                                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                min-height: 100vh;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                            }
                            .card {
                                background: white;
                                border-radius: 20px;
                                padding: 60px 50px;
                                text-align: center;
                                box-shadow: 0 20px 60px rgba(0,0,0,0.2);
                                max-width: 450px;
                                width: 90%;
                            }
                            .icon { font-size: 80px; margin-bottom: 20px; }
                            h1 { color: #2d3748; font-size: 28px; margin-bottom: 12px; }
                            p { color: #718096; font-size: 16px; line-height: 1.6; }
                            .badge {
                                display: inline-block;
                                background: #c6f6d5;
                                color: #276749;
                                padding: 6px 18px;
                                border-radius: 20px;
                                font-size: 14px;
                                margin-top: 20px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="card">
                            <div class="icon">✅</div>
                            <h1>تم تفعيل حسابك!</h1>
                            <p>أهلاً بك، حسابك الآن مفعّل ويمكنك تسجيل الدخول.</p>
                            <span class="badge">تم التحقق بنجاح</span>
                        </div>
                    </body>
                    </html>
                    """;
        } else {
            return """
                    <!DOCTYPE html>
                    <html lang="ar" dir="rtl">
                    <head>
                        <meta charset="UTF-8">
                        <title>رابط غير صالح</title>
                        <style>
                            * { margin: 0; padding: 0; box-sizing: border-box; }
                            body {
                                font-family: 'Segoe UI', sans-serif;
                                background: linear-gradient(135deg, #fc5c7d 0%, #6a3093 100%);
                                min-height: 100vh;
                                display: flex;
                                align-items: center;
                                justify-content: center;
                            }
                            .card {
                                background: white;
                                border-radius: 20px;
                                padding: 60px 50px;
                                text-align: center;
                                box-shadow: 0 20px 60px rgba(0,0,0,0.2);
                                max-width: 450px;
                                width: 90%;
                            }
                            .icon { font-size: 80px; margin-bottom: 20px; }
                            h1 { color: #2d3748; font-size: 28px; margin-bottom: 12px; }
                            p { color: #718096; font-size: 16px; line-height: 1.6; }
                            .badge {
                                display: inline-block;
                                background: #fed7d7;
                                color: #9b2c2c;
                                padding: 6px 18px;
                                border-radius: 20px;
                                font-size: 14px;
                                margin-top: 20px;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="card">
                            <div class="icon">❌</div>
                            <h1>رابط غير صالح</h1>
                            <p>هذا الرابط منتهي أو غير صحيح، تواصل مع الدعم.</p>
                            <span class="badge">فشل التحقق</span>
                        </div>
                    </body>
                    </html>
                    """;
        }
    }

}
