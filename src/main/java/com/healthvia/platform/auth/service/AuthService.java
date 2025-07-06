// auth/service/AuthService.java
package com.healthvia.platform.auth.service;

import com.healthvia.platform.auth.dto.AuthResponse;
import com.healthvia.platform.auth.dto.LoginRequest;
import com.healthvia.platform.auth.dto.RegisterAdminRequest;
import com.healthvia.platform.auth.dto.RegisterDoctorRequest;
import com.healthvia.platform.auth.dto.RegisterRequest;

public interface AuthService {
    
    // === ROLE-SPECIFIC REGISTRATIONS ===
    AuthResponse registerPatient(RegisterRequest request);
    AuthResponse registerDoctor(RegisterDoctorRequest request);
    AuthResponse registerAdmin(RegisterAdminRequest request);
    
    // === AUTHENTICATION ===
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    
    // === EMAIL & PASSWORD ===
    void verifyEmail(String token);
    void forgotPassword(String email);
    void resetPassword(String token, String newPassword);
}
