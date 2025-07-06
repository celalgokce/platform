// auth/dto/AuthResponse.java
package com.healthvia.platform.auth.dto;

import java.time.LocalDateTime;

import com.healthvia.platform.common.enums.UserRole;
import com.healthvia.platform.common.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    
    // User info
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private UserStatus status;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private LocalDateTime lastLoginDate;
    
    // Additional info
    private String message;
    private Boolean requiresAction; // Email verification, profile completion etc.
}