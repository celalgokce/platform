// controller/TestController.java - ENHANCED
package com.healthvia.platform.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthvia.platform.common.dto.ApiResponse;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.success(
            Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "message", "HealthVia Platform is running!",
                "version", "1.0.0",
                "environment", "development"
            ),
            "Sistem başarıyla çalışıyor"
        );
    }
    
    @GetMapping("/protected")
    public ApiResponse<String> protectedEndpoint() {
        return ApiResponse.success("Bu korumalı bir endpoint!", "JWT token ile erişim başarılı!");
    }
    
    // ✅ NEW: Database connection test
    @GetMapping("/db")
    public ApiResponse<Map<String, Object>> databaseTest() {
        return ApiResponse.success(
            Map.of(
                "database", "MongoDB",
                "status", "Connected",
                "timestamp", LocalDateTime.now(),
                "message", "Database connection successful"
            ),
            "Veritabanı bağlantısı başarılı"
        );
    }
    
    // ✅ NEW: Authentication test info
    @GetMapping("/auth-info")
    public ApiResponse<Map<String, Object>> authInfo() {
        return ApiResponse.success(
            Map.of(
                "emailVerificationRequired", false,
                "autoActivateUsers", true,
                "supportedRoles", new String[]{"PATIENT", "DOCTOR", "ADMIN"},
                "message", "Email verification bypass active for testing"
            ),
            "Authentication bilgileri"
        );
    }
    
    // ✅ NEW: API endpoints summary
    @GetMapping("/endpoints")
    public ApiResponse<Map<String, Object>> endpointsSummary() {
        return ApiResponse.success(
            Map.of(
                "auth", Map.of(
                    "register_patient", "POST /api/auth/register/patient",
                    "register_doctor", "POST /api/auth/register/doctor", 
                    "register_admin", "POST /api/auth/register/admin",
                    "login", "POST /api/auth/login"
                ),
                "users", Map.of(
                    "my_profile", "GET /api/users/me",
                    "all_users", "GET /api/users (admin)",
                    "search", "GET /api/users/search?searchTerm=..."
                ),
                "patients", Map.of(
                    "my_profile", "GET /api/patients/me",
                    "update_health", "PATCH /api/patients/me/health",
                    "all_patients", "GET /api/patients (admin)"
                ),
                "doctors", Map.of(
                    "my_profile", "GET /api/doctors/me",
                    "public_search", "GET /api/doctors/public/search",
                    "all_doctors", "GET /api/doctors (admin)"
                )
            ),
            "Mevcut API endpoint'leri"
        );
    }
}