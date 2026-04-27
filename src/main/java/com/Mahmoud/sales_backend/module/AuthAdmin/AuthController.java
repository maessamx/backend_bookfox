package com.Mahmoud.sales_backend.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Mahmoud.sales_backend.service.AuthService;
import com.Mahmoud.sales_backend.shared.ApiResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    // localhost:8080/auth/login
    @PostMapping("/login")
    public ApiResponse login(@RequestParam String email,
                             @RequestParam String password) {

        return authService.login(email, password);
    }
}
