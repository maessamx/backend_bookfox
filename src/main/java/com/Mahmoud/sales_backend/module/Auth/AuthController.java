package com.Mahmoud.sales_backend.module.Auth;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Mahmoud.sales_backend.model.User;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /auth/login
    @PostMapping("/login")
    public ApiResponse login(@RequestBody User user) {
        return authService.login(user.getEmail(), user.getPassword());
    }

    // POST /auth/register
    @PostMapping("/register")
    public ApiResponse register(@RequestBody User user) {
        return authService.register(user);
    }

    // GET /auth/verify-token
    @GetMapping("/verify-token")
    public ResponseEntity<String> verifyToken(@RequestParam String token) {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(authService.verifyToken(token));
    }
}
