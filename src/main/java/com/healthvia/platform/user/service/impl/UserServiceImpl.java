package com.healthvia.platform.user.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.healthvia.platform.common.constants.ErrorCodes;
import com.healthvia.platform.common.enums.Language;
import com.healthvia.platform.common.enums.UserRole;
import com.healthvia.platform.common.enums.UserStatus;
import com.healthvia.platform.common.exception.BusinessException;
import com.healthvia.platform.common.exception.ResourceNotFoundException;
import com.healthvia.platform.user.entity.User;
import com.healthvia.platform.user.repository.UserRepository;
import com.healthvia.platform.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Şifre pattern'i - en az 8 karakter, büyük harf, küçük harf, rakam, özel karakter
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    // === BASIC CRUD OPERATIONS ===

    @Override
    public User createUser(User user) {
        log.debug("Creating user with email: {}", user.getEmail());
        
        validateUserForCreation(user);
        
        // Email benzersizlik kontrolü
        if (!isEmailAvailable(user.getEmail())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Email already exists: " + user.getEmail());
        }
        
        // Telefon benzersizlik kontrolü
        if (!isPhoneAvailable(user.getPhone())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Phone already exists: " + user.getPhone());
        }
        
        // Şifreyi encode et
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Varsayılan değerler
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.PENDING_VERIFICATION);
        }
        if (user.getEmailVerified() == null) {
            user.setEmailVerified(false);
        }
        if (user.getPhoneVerified() == null) {
            user.setPhoneVerified(false);
        }
        if (user.getFailedLoginAttempts() == null) {
            user.setFailedLoginAttempts(0);
        }
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        
        return savedUser;
    }

    @Override
    public User updateUser(String id, User user) {
        log.debug("Updating user with ID: {}", id);
        
        User existingUser = findByIdOrThrow(id);
        
        // Email değişmişse benzersizlik kontrolü
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            !isEmailAvailableForUpdate(id, user.getEmail())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Email already exists: " + user.getEmail());
        }
        
        // Telefon değişmişse benzersizlik kontrolü
        if (!existingUser.getPhone().equals(user.getPhone()) && 
            !isPhoneAvailableForUpdate(id, user.getPhone())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Phone already exists: " + user.getPhone());
        }
        
        // Güncellenebilir alanları güncelle
        updateUserFields(existingUser, user);
        
        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully: {}", updatedUser.getId());
        
        return updatedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(String id) {
        return userRepository.findById(id)
            .filter(user -> !user.isDeleted());
    }

    @Override
    public void deleteUser(String id, String deletedBy) {
        log.debug("Deleting user with ID: {} by: {}", id, deletedBy);
        
        User user = findByIdOrThrow(id);
        user.markAsDeleted(deletedBy);
        userRepository.save(user);
        
        log.info("User soft deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // === AUTHENTICATION CORE METHODS ===

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmailOrPhone(String emailOrPhone) {
        return userRepository.findByEmailOrPhone(emailOrPhone)
            .filter(user -> !user.isDeleted());
    }

    // === ACCOUNT MANAGEMENT ===

    @Override
    public User updateUserStatus(String userId, UserStatus status, String updatedBy) {
        log.debug("Updating user status: {} to {}", userId, status);
        
        User user = findByIdOrThrow(userId);
        user.setStatus(status);
        
        User updatedUser = userRepository.save(user);
        log.info("User status updated: {} -> {}", userId, status);
        
        return updatedUser;
    }

    @Override
    public void verifyEmail(String userId) {
        log.debug("Verifying email for user: {}", userId);
        
        User user = findByIdOrThrow(userId);
        user.setEmailVerified(true);
        
        // Eğer kullanıcı PENDING_VERIFICATION durumundaysa, ACTIVE yap
        if (UserStatus.PENDING_VERIFICATION.equals(user.getStatus())) {
            user.setStatus(UserStatus.ACTIVE);
        }
        
        userRepository.save(user);
        log.info("Email verified for user: {}", userId);
    }

    @Override
    public void verifyPhone(String userId) {
        log.debug("Verifying phone for user: {}", userId);
        
        User user = findByIdOrThrow(userId);
        user.setPhoneVerified(true);
        
        userRepository.save(user);
        log.info("Phone verified for user: {}", userId);
    }

    // === PROFILE MANAGEMENT ===

    @Override
    public User updateNotificationPreferences(String userId, List<String> preferences) {
        log.debug("Updating notification preferences for user: {}", userId);
        
        User user = findByIdOrThrow(userId);
        
        // List'i Set'e çevir (User entity'de Set<String> kullanıyor)
        Set<String> preferenceSet = preferences != null ? Set.copyOf(preferences) : Set.of();
        user.setNotificationPreferences(preferenceSet);
        
        User updatedUser = userRepository.save(user);
        log.info("Notification preferences updated for user: {}", userId);
        
        return updatedUser;
    }

    @Override
    public User updateAvatar(String userId, String avatarUrl) {
        log.debug("Updating avatar for user: {}", userId);
        
        User user = findByIdOrThrow(userId);
        user.setAvatarUrl(avatarUrl);
        
        User updatedUser = userRepository.save(user);
        log.info("Avatar updated for user: {}", userId);
        
        return updatedUser;
    }

    @Override
    public User updateLanguagePreference(String userId, Language language) {
        log.debug("Updating language preference for user: {} to: {}", userId, language);
        
        User user = findByIdOrThrow(userId);
        user.setPreferredLanguage(language);
        
        User updatedUser = userRepository.save(user);
        log.info("Language preference updated for user: {} to: {}", userId, language);
        
        return updatedUser;
    }

    // === SEARCH & FILTER METHODS ===

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.searchUsers(searchTerm, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> searchUsersByRole(UserRole role, String searchTerm, Pageable pageable) {
        return userRepository.searchUsersByRole(role, searchTerm, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRoleAndDeletedFalse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByStatus(UserStatus status) {
        return userRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findByRoleAndStatus(UserRole role, UserStatus status, Pageable pageable) {
        if (role != null && status != null) {
            return userRepository.findByRoleAndStatus(role, status, pageable);
        } else if (role != null) {
            return userRepository.findByRole(role, pageable);
        } else if (status != null) {
            // Status için sayfalı method yok, basit yaklaşım
            return userRepository.findAll(pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findByLocation(String province, String district) {
        return userRepository.findByProvinceAndDistrictAndDeletedFalse(province, district);
    }

    // === VALIDATION METHODS ===

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmailAndDeletedFalse(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPhoneAvailable(String phone) {
        return !userRepository.existsByPhoneAndDeletedFalse(phone);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isEmailAvailableForUpdate(String userId, String email) {
        Optional<User> existingUser = userRepository.findByEmailAndDeletedFalse(email);
        return existingUser.isEmpty() || existingUser.get().getId().equals(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPhoneAvailableForUpdate(String userId, String phone) {
        Optional<User> existingUser = userRepository.findByPhone(phone);
        return existingUser.isEmpty() || 
               existingUser.get().getId().equals(userId) || 
               existingUser.get().isDeleted();
    }

    // === ADMIN OPERATIONS ===

    @Override
    public User activateUser(String userId, String activatedBy) {
        log.debug("Activating user: {} by: {}", userId, activatedBy);
        return updateUserStatus(userId, UserStatus.ACTIVE, activatedBy);
    }

    @Override
    public User suspendUser(String userId, String reason, String suspendedBy) {
        log.debug("Suspending user: {} by: {} reason: {}", userId, suspendedBy, reason);
        return updateUserStatus(userId, UserStatus.SUSPENDED, suspendedBy);
    }

    @Override
    public void permanentlyDeleteUser(String userId, String deletedBy) {
        log.debug("Permanently deleting user: {} by: {}", userId, deletedBy);
        
        User user = findByIdOrThrow(userId);
        userRepository.delete(user);
        
        log.warn("User permanently deleted: {}", userId);
    }

    @Override
    public User restoreDeletedUser(String userId, String restoredBy) {
        log.debug("Restoring deleted user: {} by: {}", userId, restoredBy);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        User user = userOpt.get();
        if (!user.isDeleted()) {
            throw new BusinessException(ErrorCodes.INVALID_USER_STATUS, "User is not deleted");
        }
        
        // Silme işlemini geri al
        user.setDeleted(false);
        user.setDeletedAt(null);
        user.setDeletedBy(null);
        user.setStatus(UserStatus.ACTIVE);
        
        User restoredUser = userRepository.save(user);
        log.info("User restored: {}", userId);
        
        return restoredUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllIncludingDeleted(Pageable pageable) {
        return userRepository.findAllIncludingDeleted(pageable);
    }

    // === MAINTENANCE METHODS ===

    @Override
    public void cleanupExpiredAccountLocks() {
        log.debug("Cleaning up expired account locks");
        
        List<User> lockedUsers = userRepository.findLockedUsers(LocalDateTime.now());
        
        List<User> usersToUpdate = lockedUsers.stream()
            .filter(user -> user.getAccountLockedUntil() != null && 
                           user.getAccountLockedUntil().isBefore(LocalDateTime.now()))
            .peek(user -> {
                user.setAccountLockedUntil(null);
                user.resetFailedLoginAttempts();
            })
            .toList();
        
        if (!usersToUpdate.isEmpty()) {
            userRepository.saveAll(usersToUpdate);
            log.info("Cleaned up {} expired account locks", usersToUpdate.size());
        }
    }

    @Override
    public void cleanupExpiredVerificationTokens() {
        // Bu method şimdilik placeholder
        // Gerçek implementasyon için verification token entity'si gerekli
        log.debug("cleanupExpiredVerificationTokens called - placeholder implementation");
    }

    // === HELPER METHODS ===

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String userId) {
        return userRepository.existsById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserActive(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(user -> UserStatus.ACTIVE.equals(user.getStatus()) && !user.isDeleted())
                     .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserDeleted(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(User::isDeleted).orElse(false);
    }

    // === PRIVATE HELPER METHODS ===

    private User findByIdOrThrow(String id) {
        return userRepository.findById(id)
            .filter(user -> !user.isDeleted())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private void validateUserForCreation(User user) {
        // Zorunlu alanlar kontrolü
        if (!StringUtils.hasText(user.getEmail())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Email is required");
        }
        if (!StringUtils.hasText(user.getPhone())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Phone is required");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Password is required");
        }
        if (user.getRole() == null) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "User role is required");
        }
        if (!StringUtils.hasText(user.getFirstName())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "First name is required");
        }
        if (!StringUtils.hasText(user.getLastName())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Last name is required");
        }
        
        // Şifre kuvvetlilik kontrolü
        if (!isPasswordStrong(user.getPassword())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, 
                "Password must be at least 8 characters long and contain uppercase, lowercase, number and special character");
        }
        
        // Email format kontrolü (additional check)
        if (!isValidEmail(user.getEmail())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Invalid email format");
        }
    }

    private void updateUserFields(User existingUser, User newUser) {
        // Temel bilgiler
        if (StringUtils.hasText(newUser.getFirstName())) {
            existingUser.setFirstName(newUser.getFirstName());
        }
        if (StringUtils.hasText(newUser.getLastName())) {
            existingUser.setLastName(newUser.getLastName());
        }
        if (StringUtils.hasText(newUser.getEmail())) {
            existingUser.setEmail(newUser.getEmail());
        }
        if (StringUtils.hasText(newUser.getPhone())) {
            existingUser.setPhone(newUser.getPhone());
        }
        
        // Opsiyonel alanlar
        if (newUser.getGender() != null) {
            existingUser.setGender(newUser.getGender());
        }
        if (newUser.getBirthDate() != null) {
            existingUser.setBirthDate(newUser.getBirthDate());
        }
        if (StringUtils.hasText(newUser.getProvince())) {
            existingUser.setProvince(newUser.getProvince());
        }
        if (StringUtils.hasText(newUser.getDistrict())) {
            existingUser.setDistrict(newUser.getDistrict());
        }
        if (newUser.getPreferredLanguage() != null) {
            existingUser.setPreferredLanguage(newUser.getPreferredLanguage());
        }
        if (StringUtils.hasText(newUser.getAvatarUrl())) {
            existingUser.setAvatarUrl(newUser.getAvatarUrl());
        }
        if (newUser.getNotificationPreferences() != null) {
            existingUser.setNotificationPreferences(newUser.getNotificationPreferences());
        }
        
        // GDPR consent alanları
        if (newUser.getGdprConsent() != null) {
            existingUser.setGdprConsent(newUser.getGdprConsent());
            if (newUser.getGdprConsent()) {
                existingUser.setGdprConsentDate(LocalDateTime.now());
            }
        }
        if (newUser.getMarketingConsent() != null) {
            existingUser.setMarketingConsent(newUser.getMarketingConsent());
        }
        if (newUser.getDataProcessingConsent() != null) {
            existingUser.setDataProcessingConsent(newUser.getDataProcessingConsent());
        }
        if (newUser.getDataSharingConsent() != null) {
            existingUser.setDataSharingConsent(newUser.getDataSharingConsent());
        }
    }

    private boolean isPasswordStrong(String password) {
        return StringUtils.hasText(password) && PASSWORD_PATTERN.matcher(password).matches();
    }

    private boolean isValidEmail(String email) {
        return StringUtils.hasText(email) && email.contains("@") && email.contains(".");
    }
}