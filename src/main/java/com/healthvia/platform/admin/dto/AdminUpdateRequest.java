// admin/dto/AdminUpdateRequest.java
package com.healthvia.platform.admin.dto;

import java.util.Set;

import com.healthvia.platform.admin.entity.Admin;
import com.healthvia.platform.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateRequest {
    
    private String firstName;
    private String lastName;
    private String phone;
    private User.Gender gender;
    private String province;
    private String district;
    
    // Admin specific
    private String department;
    private String jobTitle;
    private Admin.AdminLevel adminLevel;
    private Set<Admin.AdminPermission> permissions;
    private String supervisorId;
    
    // Capabilities
    private Boolean canManageUsers;
    private Boolean canManageDoctors;
    private Boolean canManageClinics;
    private Boolean canViewReports;
    private Boolean canManageSystem;
}
