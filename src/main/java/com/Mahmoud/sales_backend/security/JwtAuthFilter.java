package com.Mahmoud.sales_backend.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.Mahmoud.sales_backend.shared.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_PATHS =
            Arrays.asList("/auth/login", "/auth/register");

    private static final List<String> PUBLIC_CATALOG =
            Arrays.asList("/books", "/supplies", "/authors");

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        for (String p : PUBLIC_PATHS) {
            if (path.startsWith(p)) {
                return true;
            }
        }

        if ("GET".equals(request.getMethod())) {
            for (String p : PUBLIC_CATALOG) {
                if (path.startsWith(p)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || authHeader.isEmpty()) {
                sendError(response, "Missing Authorization header");
                return;
            }

            if (!authHeader.startsWith("Bearer ")) {
                sendError(response, "Invalid Authorization format");
                return;
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.isTokenValid(token)) {
                sendError(response, "Invalid or expired token");
                return;
            }

            Integer userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            if (userId == null) {
                sendError(response, "Invalid token payload");
                return;
            }

            request.setAttribute("userId", userId);
            request.setAttribute("role", role);
            UserContext.setUserId(userId);
            UserContext.setRole(role);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            sendError(response, "Authentication failed");
        } finally {
            UserContext.clear();
        }
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
    }
}
