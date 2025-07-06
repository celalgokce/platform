// admin/dto/AdminDashboardDto.java
package com.healthvia.platform.admin.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {
    
    // Admin info
    private AdminDto adminInfo;
    
    // Statistics
    private Map<String, Long> userStatistics;
    private Map<String, Long> departmentStatistics;
    private Map<String, Long> actionStatistics;
    
    // Recent activities
    private List<RecentActivityDto> recentActivities;
    
    // System status
    private SystemStatusDto systemStatus;
    
    // Quick actions available
    private List<String> availableActions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivityDto {
        private String action;
        private String description;
        private LocalDateTime timestamp;
        private String performedBy;
        private String targetType;
        private String targetId;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemStatusDto {
        private String status;
        private LocalDateTime lastUpdate;
        private Map<String, String> healthChecks;
        private List<String> alerts;
    }
}