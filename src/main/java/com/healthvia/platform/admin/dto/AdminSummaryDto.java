// admin/dto/AdminSummaryDto.java
package com.healthvia.platform.admin.dto;

import java.time.LocalDateTime;

import com.healthvia.platform.admin.entity.Admin;
import com.healthvia.platform.common.enums.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSummaryDto {
    
    private String id;
    private String fullName;
    private String email;
    private String department;
    private String jobTitle;
    private Admin.AdminLevel adminLevel;
    private String employeeId;
    private UserStatus status;
    private LocalDateTime lastAdminAction;
    private Integer totalActionsPerformed;
    private boolean isActive;
    
    public static AdminSummaryDto fromEntity(Admin admin) {
        if (admin == null) return null;
        
        return AdminSummaryDto.builder()
            .id(admin.getId())
            .fullName(admin.getFullName())
            .email(admin.getEmail())
            .department(admin.getDepartment())
            .jobTitle(admin.getJobTitle())
            .adminLevel(admin.getAdminLevel())
            .employeeId(admin.getEmployeeId())
            .status(admin.getStatus())
            .lastAdminAction(admin.getLastAdminAction())
            .totalActionsPerformed(admin.getTotalActionsPerformed())
            .isActive(admin.getLastAdminAction() != null && 
                     admin.getLastAdminAction().isAfter(LocalDateTime.now().minusDays(7)))
            .build();
    }
}
