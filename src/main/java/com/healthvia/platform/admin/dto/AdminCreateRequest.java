// admin/dto/AdminCreateRequest.java
package com.healthvia.platform.admin.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.healthvia.platform.admin.entity.Admin;
import com.healthvia.platform.user.entity.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateRequest {
    
    @NotBlank(message = "Ad boş olamaz")
    private String firstName;
    
    @NotBlank(message = "Soyad boş olamaz")
    private String lastName;
    
    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;
    
    @NotBlank(message = "Telefon numarası boş olamaz")
    private String phone;
    
    @NotBlank(message = "Şifre boş olamaz")
    private String password;
    
    private User.Gender gender;
    private String province;
    private String district;
    
    @NotBlank(message = "Departman boş olamaz")
    private String department;
    
    private String jobTitle;
    
    @NotBlank(message = "Çalışan numarası boş olamaz")
    private String employeeId;
    
    private Admin.AdminLevel adminLevel;
    private Set<Admin.AdminPermission> permissions;
    private String supervisorId;
    private LocalDateTime hireDate;
    
    // Capabilities
    private Boolean canManageUsers;
    private Boolean canManageDoctors;
    private Boolean canManageClinics;
    private Boolean canViewReports;
    private Boolean canManageSystem;
}