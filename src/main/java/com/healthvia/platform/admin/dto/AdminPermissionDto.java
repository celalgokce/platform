// admin/dto/AdminPermissionDto.java
package com.healthvia.platform.admin.dto;

import java.util.List;
import java.util.Set;

import com.healthvia.platform.admin.entity.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminPermissionDto {
    
    private String adminId;
    private String adminName;
    private String department;
    private Admin.AdminLevel adminLevel;
    
    // Core capabilities
    private Boolean canManageUsers;
    private Boolean canManageDoctors;
    private Boolean canManageClinics;
    private Boolean canViewReports;
    private Boolean canManageSystem;
    
    // Specific permissions
    private Set<Admin.AdminPermission> permissions;
    private List<String> permissionDescriptions;
    
    public static AdminPermissionDto fromEntity(Admin admin) {
        if (admin == null) return null;
        
        List<String> descriptions = admin.getPermissions() != null ?
            admin.getPermissions().stream()
                .map(Admin.AdminPermission::getDisplayName)
                .toList() : List.of();
        
        return AdminPermissionDto.builder()
            .adminId(admin.getId())
            .adminName(admin.getFullName())
            .department(admin.getDepartment())
            .adminLevel(admin.getAdminLevel())
            .canManageUsers(admin.getCanManageUsers())
            .canManageDoctors(admin.getCanManageDoctors())
            .canManageClinics(admin.getCanManageClinics())
            .canViewReports(admin.getCanViewReports())
            .canManageSystem(admin.getCanManageSystem())
            .permissions(admin.getPermissions())
            .permissionDescriptions(descriptions)
            .build();
    }
}