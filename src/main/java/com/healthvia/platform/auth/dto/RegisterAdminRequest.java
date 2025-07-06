// auth/dto/RegisterAdminRequest.java
package com.healthvia.platform.auth.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.healthvia.platform.admin.entity.Admin;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RegisterAdminRequest extends RegisterRequest {
    
    @NotBlank(message = "Departman bilgisi boş olamaz")
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
