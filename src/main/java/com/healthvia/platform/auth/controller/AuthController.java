
package com.healthvia.platform.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.healthvia.platform.auth.dto.AuthResponse;
import com.healthvia.platform.auth.dto.LoginRequest;
import com.healthvia.platform.auth.dto.RegisterAdminRequest;
import com.healthvia.platform.auth.dto.RegisterDoctorRequest;
import com.healthvia.platform.auth.dto.RegisterRequest;
import com.healthvia.platform.auth.service.AuthService;
import com.healthvia.platform.common.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // === ROLE-SPECIFIC REGISTRATIONS ===
    
    @PostMapping("/register/patient")
    public ApiResponse<AuthResponse> registerPatient(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.registerPatient(request);
        return ApiResponse.success(response, "Hasta kaydı başarılı. Email doğrulaması için mailinizi kontrol edin.");
    }

    @PostMapping("/register/doctor")
    public ApiResponse<AuthResponse> registerDoctor(@Valid @RequestBody RegisterDoctorRequest request) {
        AuthResponse response = authService.registerDoctor(request);
        return ApiResponse.success(response, "Doktor kaydı başarılı. Hesabınız inceleme altındadır.");
    }

    @PostMapping("/register/admin")
    public ApiResponse<AuthResponse> registerAdmin(@Valid @RequestBody RegisterAdminRequest request) {
        AuthResponse response = authService.registerAdmin(request);
        return ApiResponse.success(response, "Admin kaydı başarılı.");
    }

    // === AUTHENTICATION ===
    
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.success(response, "Giriş başarılı");
    }

    // === TOKEN OPERATIONS ===
    
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@RequestBody String refreshToken) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ApiResponse.success(response, "Token yenilendi");
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        // JWT stateless olduğu için client-side logout
        return ApiResponse.success("Çıkış başarılı");
    }

    // === EMAIL & PASSWORD OPERATIONS ===
    
    @PostMapping("/verify-email")
    public ApiResponse<Void> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ApiResponse.success("Email doğrulandı");
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ApiResponse.success("Şifre sıfırlama bağlantısı email adresinize gönderildi");
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(
            @RequestParam String token, 
            @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ApiResponse.success("Şifre başarıyla güncellendi");
    }
}