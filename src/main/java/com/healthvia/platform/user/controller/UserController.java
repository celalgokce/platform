// user/controller/UserController.java
package com.healthvia.platform.user.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.healthvia.platform.common.dto.ApiResponse;
import com.healthvia.platform.common.enums.UserRole;
import com.healthvia.platform.common.enums.UserStatus;
import com.healthvia.platform.common.util.SecurityUtils;
import com.healthvia.platform.user.entity.User;
import com.healthvia.platform.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // === PUBLIC ENDPOINTS ===
    
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<User> getCurrentUser() {
        String userId = SecurityUtils.getCurrentUserId();
        return userService.findById(userId)
            .map(ApiResponse::success)
            .orElse(ApiResponse.error("User not found"));
    }

    // === ADMIN ONLY ENDPOINTS ===
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<User>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        return ApiResponse.success(users);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<User>> searchUsers(
            @RequestParam String searchTerm,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<User> users = userService.searchUsers(searchTerm, pageable);
        return ApiResponse.success(users);
    }
    
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<User>> getUsersByRole(
            @PathVariable UserRole role,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<User> users = userService.findByRoleAndStatus(role, null, pageable);
        return ApiResponse.success(users);
    }
    
    @GetMapping("/by-status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<User>> getUsersByStatus(
            @PathVariable UserStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<User> users = userService.findByRoleAndStatus(null, status, pageable);
        return ApiResponse.success(users);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ApiResponse<User> getUserById(@PathVariable String id) {
        return userService.findById(id)
            .map(ApiResponse::success)
            .orElse(ApiResponse.error("User not found"));
    }

    // === USER MANAGEMENT (ADMIN) ===
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ApiResponse.success(createdUser, "User created successfully");
    }
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ApiResponse<User> updateUser(
            @PathVariable String id, 
            @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ApiResponse.success(updatedUser, "User updated successfully");
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable String id) {
        String deletedBy = SecurityUtils.getCurrentUserId();
        userService.deleteUser(id, deletedBy);
        return ApiResponse.success("User deleted successfully");
    }

    // === USER STATUS MANAGEMENT ===
    
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<User> activateUser(@PathVariable String id) {
        String activatedBy = SecurityUtils.getCurrentUserId();
        User activatedUser = userService.activateUser(id, activatedBy);
        return ApiResponse.success(activatedUser, "User activated successfully");
    }
    
    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<User> suspendUser(
            @PathVariable String id,
            @RequestParam String reason) {
        String suspendedBy = SecurityUtils.getCurrentUserId();
        User suspendedUser = userService.suspendUser(id, reason, suspendedBy);
        return ApiResponse.success(suspendedUser, "User suspended successfully");
    }

    // === EMAIL/PHONE VERIFICATION ===
    
    @PostMapping("/{id}/verify-email")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ApiResponse<Void> verifyEmail(@PathVariable String id) {
        userService.verifyEmail(id);
        return ApiResponse.success("Email verified successfully");
    }
    
    @PostMapping("/{id}/verify-phone")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ApiResponse<Void> verifyPhone(@PathVariable String id) {
        userService.verifyPhone(id);
        return ApiResponse.success("Phone verified successfully");
    }

    // === VALIDATION ENDPOINTS ===
    
    @GetMapping("/check-email")
    public ApiResponse<Boolean> checkEmailAvailability(@RequestParam String email) {
        boolean available = userService.isEmailAvailable(email);
        return ApiResponse.success(available);
    }
    
    @GetMapping("/check-phone")
    public ApiResponse<Boolean> checkPhoneAvailability(@RequestParam String phone) {
        boolean available = userService.isPhoneAvailable(phone);
        return ApiResponse.success(available);
    }

    // === LOCATION BASED QUERIES ===
    
    @GetMapping("/by-location")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<User>> getUsersByLocation(
            @RequestParam String province,
            @RequestParam(required = false) String district,
            @PageableDefault(size = 20) Pageable pageable) {
        
        // Location filtreleme için custom search yapmamız gerekiyor
        // Şimdilik basit bir yaklaşım:
        Page<User> users = userService.searchUsers(province, pageable);
        return ApiResponse.success(users);
    }

    // === STATISTICS (ADMIN) ===
    
    @GetMapping("/statistics/count-by-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Long> countUsersByRole(@RequestParam UserRole role) {
        // Bu method'u UserService'e eklemek gerek
        long count = userService.findByRole(role).size();
        return ApiResponse.success(count);
    }
    
    @GetMapping("/statistics/count-by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Long> countUsersByStatus(@RequestParam UserStatus status) {
        long count = userService.findByStatus(status).size();
        return ApiResponse.success(count);
    }

    // === MAINTENANCE (ADMIN) ===
    
    @PostMapping("/maintenance/cleanup-expired-locks")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> cleanupExpiredAccountLocks() {
        userService.cleanupExpiredAccountLocks();
        return ApiResponse.success("Expired account locks cleaned up");
    }
    
    @PostMapping("/maintenance/cleanup-verification-tokens")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> cleanupExpiredVerificationTokens() {
        userService.cleanupExpiredVerificationTokens();
        return ApiResponse.success("Expired verification tokens cleaned up");
    }

    // === SOFT DELETE RECOVERY (ADMIN) ===
    
    @GetMapping("/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Page<User>> getDeletedUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<User> deletedUsers = userService.findAllIncludingDeleted(pageable);
        // Filter only deleted ones - bu logic'i service'e taşımak daha iyi
        return ApiResponse.success(deletedUsers);
    }
    
    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<User> restoreDeletedUser(@PathVariable String id) {
        String restoredBy = SecurityUtils.getCurrentUserId();
        User restoredUser = userService.restoreDeletedUser(id, restoredBy);
        return ApiResponse.success(restoredUser, "User restored successfully");
    }
    
    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> permanentlyDeleteUser(@PathVariable String id) {
        String deletedBy = SecurityUtils.getCurrentUserId();
        userService.permanentlyDeleteUser(id, deletedBy);
        return ApiResponse.success("User permanently deleted");
    }
}