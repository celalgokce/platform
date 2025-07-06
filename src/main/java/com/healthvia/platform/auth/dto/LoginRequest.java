// auth/dto/LoginRequest.java
package com.healthvia.platform.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Email veya telefon boş olamaz")
    private String username; // Email veya telefon
    
    @NotBlank(message = "Şifre boş olamaz")
    private String password;
}