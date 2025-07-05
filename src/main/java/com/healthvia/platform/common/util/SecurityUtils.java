// common/util/SecurityUtils.java
package com.healthvia.platform.common.util;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.healthvia.platform.auth.security.UserPrincipal;
import com.healthvia.platform.common.enums.UserRole;

public class SecurityUtils {
    
    private SecurityUtils() {
        throw new IllegalStateException("Utility class");
    }
    
    public static Optional<Authentication> getCurrentAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }
    
    public static Optional<UserPrincipal> getCurrentUser() {
        return getCurrentAuthentication()
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .filter(principal -> principal instanceof UserPrincipal)
            .map(UserPrincipal.class::cast);
    }
    
    public static String getCurrentUserId() {
        return getCurrentUser()
            .map(UserPrincipal::getId)
            .orElseThrow(() -> new IllegalStateException("User not authenticated"));
    }
    
    public static String getCurrentUserIdOrNull() {
        return getCurrentUser()
            .map(UserPrincipal::getId)
            .orElse(null);
    }
    
    public static UserRole getCurrentUserRole() {
        return getCurrentUser()
            .map(UserPrincipal::getRole)
            .orElseThrow(() -> new IllegalStateException("User not authenticated"));
    }
    
    public static boolean isAuthenticated() {
        return getCurrentAuthentication()
            .map(Authentication::isAuthenticated)
            .orElse(false);
    }
    
    public static boolean hasRole(UserRole role) {
        return getCurrentUser()
            .map(user -> user.getRole() == role)
            .orElse(false);
    }
    
    public static boolean isAdmin() {
        return hasRole(UserRole.ADMIN);
    }
    
    public static boolean isDoctor() {
        return hasRole(UserRole.DOCTOR);
    }
    
    public static boolean isPatient() {
        return hasRole(UserRole.PATIENT);
    }
}