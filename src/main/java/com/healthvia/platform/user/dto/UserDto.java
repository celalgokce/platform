// user/dto/UserDto.java
package com.healthvia.platform.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.healthvia.platform.common.enums.Language;
import com.healthvia.platform.common.enums.UserRole;
import com.healthvia.platform.common.enums.UserStatus;
import com.healthvia.platform.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private User.Gender gender;
    private LocalDate birthDate;
    private String province;
    private String district;
    private UserRole role;
    private UserStatus status;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Integer profileCompletionRate;
    private LocalDateTime lastLoginDate;
    private Language preferredLanguage;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // === BUSINESS METHODS ===
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public int getAge() {
        if (birthDate == null) return 0;
        return LocalDate.now().getYear() - birthDate.getYear();
    }
    
    public boolean isProfileComplete() {
        return profileCompletionRate != null && profileCompletionRate >= 80;
    }
    
    // === FACTORY METHODS ===
    
    public static UserDto fromEntity(User user) {
        if (user == null) return null;
        
        return UserDto.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .gender(user.getGender())
            .birthDate(user.getBirthDate())
            .province(user.getProvince())
            .district(user.getDistrict())
            .role(user.getRole())
            .status(user.getStatus())
            .emailVerified(user.getEmailVerified())
            .phoneVerified(user.getPhoneVerified())
            .profileCompletionRate(user.getProfileCompletionRate())
            .lastLoginDate(user.getLastLoginDate())
            .preferredLanguage(user.getPreferredLanguage())
            .avatarUrl(user.getAvatarUrl())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
    
    public static UserDto fromEntityBasic(User user) {
        if (user == null) return null;
        
        return UserDto.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .phone(user.getPhone())
            .role(user.getRole())
            .status(user.getStatus())
            .avatarUrl(user.getAvatarUrl())
            .build();
    }
    
    public User toEntity() {
        return User.builder()
            .id(this.id)
            .firstName(this.firstName)
            .lastName(this.lastName)
            .email(this.email)
            .phone(this.phone)
            .gender(this.gender)
            .birthDate(this.birthDate)
            .province(this.province)
            .district(this.district)
            .role(this.role)
            .status(this.status)
            .emailVerified(this.emailVerified)
            .phoneVerified(this.phoneVerified)
            .profileCompletionRate(this.profileCompletionRate)
            .lastLoginDate(this.lastLoginDate)
            .preferredLanguage(this.preferredLanguage)
            .avatarUrl(this.avatarUrl)
            .build();
    }
}