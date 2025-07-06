// user/service/impl/UserServiceImpl.java
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

    // Password regex: En az 8 karakter, 1 büyük, 1 küçük, 1 rakam, 1 özel karakter
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    // === BASIC CRUD OPERATIONS ===

    @Override
    public User createUser(User user) {
        log.info("Creating new user with email: {}", user.getEmail());
        
        // Validation
        validateUserForCreation(user);
        
        // Email ve telefon benzersizlik kontrolü
        if (!isEmailAvailable(user.getEmail())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Email already exists");
        }
        
        if (!isPhoneAvailable(user.getPhone())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Phone already exists");
        }
        
        // Şifre hashleme
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Default değerler
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.PENDING_VERIFICATION);
        }
        
        // Profil tamamlanma oranı hesapla
        int completionRate = calculateProfileCompletionRate(user);
        user.setProfileCompletionRate(completionRate);
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        
        return savedUser;
    }

    @Override
    public User updateUser(String id, User user) {
        log.info("Updating user with ID: {}", id);
        
        User existingUser = findByIdOrThrow(id);
        
        // Email ve telefon değiştiriliyorsa benzersizlik kontrolü
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            !isEmailAvailableForUpdate(id, user.getEmail())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Email already exists");
        }
        
        if (!existingUser.getPhone().equals(user.getPhone()) && 
            !isPhoneAvailableForUpdate(id, user.getPhone())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Phone already exists");
        }
        
        // Güncelleme
        updateUserFields(existingUser, user);
        
        // Profil tamamlanma oranını yeniden hesapla
        int completionRate = calculateProfileCompletionRate(existingUser);
        existingUser.setProfileCompletionRate(completionRate);
        
        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully: {}", id);
        
        return updatedUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(String id) {
        return userRepository.findById(id).filter(user -> !user.isDeleted());
    }

    @Override
    public void deleteUser(String id, String deletedBy) {
        log.info("Soft deleting user: {} by: {}", id, deletedBy);
        
        User user = findByIdOrThrow(id);
        user.markAsDeleted(deletedBy);
        
        userRepository.save(user);
        log.info("User deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    // === AUTHENTICATION METHODS ===

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailAndDeletedFalse(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmailOrPhone(String emailOrPhone) {
        return userRepository.findByEmailOrPhone(emailOrPhone);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validatePassword(String userId, String rawPassword) {
        User user = findByIdOrThrow(userId);
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    @Override
    public void changePassword(String userId, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", userId);
        
        User user = findByIdOrThrow(userId);
        
        // Eski şifre kontrolü
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Invalid old password");
        }
        
        // Yeni şifre kuvvetlilik kontrolü
        if (!isPasswordStrong(newPassword)) {
            throw new BusinessException(ErrorCodes.INVALID_USER_STATUS, "Password is not strong enough");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Password changed successfully for user: {}", userId);
    }

    @Override
    public void resetPassword(String email) {
        log.info("Resetting password for email: {}", email);
        
        User user = findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        
        // Geçici şifre oluştur (gerçek uygulamada email gönderilir)
        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        
        userRepository.save(user);
        
        // TODO: Email gönderme servisi entegrasyonu
        log.info("Password reset initiated for user: {}", user.getId());
    }

    @Override
    public void updateLastLogin(String userId) {
        User user = findByIdOrThrow(userId);
        user.updateLastLogin();
        userRepository.save(user);
    }

    // === ACCOUNT MANAGEMENT ===

    @Override
    public User updateUserStatus(String userId, UserStatus status, String updatedBy) {
        log.info("Updating user status: {} to: {} by: {}", userId, status, updatedBy);
        
        User user = findByIdOrThrow(userId);
        user.setStatus(status);
        
        User updatedUser = userRepository.save(user);
        log.info("User status updated successfully: {}", userId);
        
        return updatedUser;
    }

    @Override
    public void verifyEmail(String userId) {
        log.info("Verifying email for user: {}", userId);
        
        User user = findByIdOrThrow(userId);
        user.setEmailVerified(true);
        
        // Email doğrulandıysa ve status PENDING_VERIFICATION ise ACTIVE yap
        if (UserStatus.PENDING_VERIFICATION.equals(user.getStatus())) {
            user.setStatus(UserStatus.ACTIVE);
        }
        
        userRepository.save(user);
        log.info("Email verified successfully for user: {}", userId);
    }

    @Override
    public void verifyPhone(String userId) {
        log.info("Verifying phone for user: {}", userId);
        
        User user = findByIdOrThrow(userId);
        user.setPhoneVerified(true);
        
        userRepository.save(user);
        log.info("Phone verified successfully for user: {}", userId);
    }

    @Override
    public void lockAccount(String userId, int durationMinutes, String reason) {
        log.info("Locking account: {} for {} minutes. Reason: {}", userId, durationMinutes, reason);
        
        User user = findByIdOrThrow(userId);
        user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(durationMinutes));
        
        userRepository.save(user);
        log.info("Account locked successfully: {}", userId);
    }

    @Override
    public void unlockAccount(String userId) {
        log.info("Unlocking account: {}", userId);
        
        User user = findByIdOrThrow(userId);
        user.setAccountLockedUntil(null);
        user.resetFailedLoginAttempts();
        
        userRepository.save(user);
        log.info("Account unlocked successfully: {}", userId);
    }

    @Override
    public void recordFailedLoginAttempt(String userId) {
        User user = findByIdOrThrow(userId);
        user.incrementFailedLoginAttempts();
        
        userRepository.save(user);
        log.warn("Failed login attempt recorded for user: {}. Total attempts: {}", 
                userId, user.getFailedLoginAttempts());
    }

    @Override
    public void resetFailedLoginAttempts(String userId) {
        User user = findByIdOrThrow(userId);
        user.resetFailedLoginAttempts();
        
        userRepository.save(user);
        log.info("Failed login attempts reset for user: {}", userId);
    }

    // === PROFILE MANAGEMENT ===

    @Override
    public int calculateAndUpdateProfileCompletion(String userId) {
        User user = findByIdOrThrow(userId);
        int completionRate = calculateProfileCompletionRate(user);
        
        user.setProfileCompletionRate(completionRate);
        userRepository.save(user);
        
        return completionRate;
    }

    @Override
    public User updateAvatar(String userId, String avatarUrl) {
        log.info("Updating avatar for user: {}", userId);
        
        User user = findByIdOrThrow(userId);
        user.setAvatarUrl(avatarUrl);
        
        return userRepository.save(user);
    }

    @Override
    public User updateLanguagePreference(String userId, Language language) {
        log.info("Updating language preference for user: {} to: {}", userId, language);
        
        User user = findByIdOrThrow(userId);
        user.setPreferredLanguage(language);
        
        return userRepository.save(user);
    }

    @Override
    public User updateNotificationPreferences(String userId, List<String> preferences) {
        log.info("Updating notification preferences for user: {}", userId);
        
        User user = findByIdOrThrow(userId);
        user.setNotificationPreferences(Set.copyOf(preferences));
        
        return userRepository.save(user);
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
        return userRepository.findByRoleAndStatus(role, status, pageable);
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
        return existingUser.isEmpty() || existingUser.get().getId().equals(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPasswordStrong(String password) {
        return StringUtils.hasText(password) && PASSWORD_PATTERN.matcher(password).matches();
    }

    // === GDPR & CONSENT METHODS ===

    @Override
    public void updateGdprConsent(String userId, boolean consent) {
        User user = findByIdOrThrow(userId);
        user.setGdprConsent(consent);
        if (consent) {
            user.setGdprConsentDate(LocalDateTime.now());
        }
        userRepository.save(user);
    }

    @Override
    public void updateDataProcessingConsent(String userId, boolean consent) {
        User user = findByIdOrThrow(userId);
        user.setDataProcessingConsent(consent);
        userRepository.save(user);
    }

    @Override
    public void updateMarketingConsent(String userId, boolean consent) {
        User user = findByIdOrThrow(userId);
        user.setMarketingConsent(consent);
        userRepository.save(user);
    }

    @Override
    public void updateDataSharingConsent(String userId, boolean consent) {
        User user = findByIdOrThrow(userId);
        user.setDataSharingConsent(consent);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersWithoutGdprConsent() {
        return userRepository.findByGdprConsentFalseAndDeletedFalse();
    }

    // === ANALYTICS & STATISTICS ===

    @Override
    @Transactional(readOnly = true)
    public long countByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(UserStatus status) {
        return userRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnverifiedEmails() {
        return userRepository.countByEmailVerifiedFalseAndDeletedFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersRegisteredBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return userRepository.countUsersRegisteredBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findInactiveUsers(LocalDateTime lastLoginBefore) {
        return userRepository.findInactiveUsersSince(lastLoginBefore);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findNeverLoggedInUsers(LocalDateTime createdBefore) {
        return userRepository.findNeverLoggedInUsersBefore(createdBefore);
    }

    // === BULK OPERATIONS ===

    @Override
    public List<User> createUsers(List<User> users) {
        log.info("Creating {} users in bulk", users.size());
        
        users.forEach(this::validateUserForCreation);
        users.forEach(user -> user.setPassword(passwordEncoder.encode(user.getPassword())));
        
        List<User> savedUsers = userRepository.saveAll(users);
        log.info("Bulk user creation completed: {} users", savedUsers.size());
        
        return savedUsers;
    }

    @Override
    public void updateUsersStatus(List<String> userIds, UserStatus status, String updatedBy) {
        log.info("Updating status for {} users to: {} by: {}", userIds.size(), status, updatedBy);
        
        List<User> users = userRepository.findAllById(userIds);
        users.forEach(user -> user.setStatus(status));
        
        userRepository.saveAll(users);
        log.info("Bulk status update completed for {} users", users.size());
    }

    @Override
    public void deleteUsers(List<String> userIds, String deletedBy) {
        log.info("Soft deleting {} users by: {}", userIds.size(), deletedBy);
        
        List<User> users = userRepository.findAllById(userIds);
        users.forEach(user -> user.markAsDeleted(deletedBy));
        
        userRepository.saveAll(users);
        log.info("Bulk deletion completed for {} users", users.size());
    }

    // === ADMIN OPERATIONS ===

    @Override
    public User activateUser(String userId, String activatedBy) {
        log.info("Activating user: {} by: {}", userId, activatedBy);
        return updateUserStatus(userId, UserStatus.ACTIVE, activatedBy);
    }

    @Override
    public User suspendUser(String userId, String reason, String suspendedBy) {
        log.info("Suspending user: {} by: {}. Reason: {}", userId, suspendedBy, reason);
        return updateUserStatus(userId, UserStatus.SUSPENDED, suspendedBy);
    }

    @Override
    public void permanentlyDeleteUser(String userId, String deletedBy) {
        log.warn("Permanently deleting user: {} by: {}", userId, deletedBy);
        
        User user = findByIdOrThrow(userId);
        userRepository.delete(user);
        
        log.warn("User permanently deleted: {}", userId);
    }

    @Override
    public User restoreDeletedUser(String userId, String restoredBy) {
        log.info("Restoring deleted user: {} by: {}", userId, restoredBy);
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        User user = userOpt.get();
        if (!user.isDeleted()) {
            throw new BusinessException(ErrorCodes.INVALID_USER_STATUS, "User is not deleted");
        }
        
        user.setDeleted(false);
        user.setDeletedAt(null);
        user.setDeletedBy(null);
        user.setStatus(UserStatus.ACTIVE);
        
        User restoredUser = userRepository.save(user);
        log.info("User restored successfully: {}", userId);
        
        return restoredUser;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllIncludingDeleted(Pageable pageable) {
        return userRepository.findAllIncludingDeleted(pageable);
    }

    // === NOTIFICATION METHODS ===

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersWithMarketingConsent() {
        return userRepository.findByMarketingConsentTrueAndDeletedFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersByLanguage(Language language) {
        return userRepository.findByPreferredLanguageAndDeletedFalse(language);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersByNotificationPreference(String preference) {
        // MongoDB query for array field containing specific value
        return userRepository.findAll().stream()
            .filter(user -> !user.isDeleted())
            .filter(user -> user.getNotificationPreferences() != null && 
                          user.getNotificationPreferences().contains(preference))
            .toList();
    }

    // === SECURITY METHODS ===

    @Override
    @Transactional(readOnly = true)
    public List<User> findLockedAccounts() {
        return userRepository.findLockedUsers(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findUsersWithFailedAttempts(int minFailedAttempts) {
        return userRepository.findUsersWithFailedAttempts(minFailedAttempts);
    }

    @Override
    public void logSecurityEvent(String userId, String eventType, String description) {
        log.warn("Security event for user {}: {} - {}", userId, eventType, description);
        // TODO: Security event logging service entegrasyonu
    }

    // === MAINTENANCE METHODS ===

    @Override
    public void cleanupExpiredAccountLocks() {
        log.info("Cleaning up expired account locks");
        
        List<User> lockedUsers = userRepository.findLockedUsers(LocalDateTime.now());
        
        // Süresi dolmuş kilitleri temizle
        lockedUsers.stream()
            .filter(user -> user.getAccountLockedUntil() != null && 
                          user.getAccountLockedUntil().isBefore(LocalDateTime.now()))
            .forEach(user -> {
                user.setAccountLockedUntil(null);
                user.resetFailedLoginAttempts();
            });
        
        if (!lockedUsers.isEmpty()) {
            userRepository.saveAll(lockedUsers);
            log.info("Cleaned up {} expired account locks", lockedUsers.size());
        }
    }

    @Override
    public void cleanupExpiredVerificationTokens() {
        log.info("Cleaning up expired verification tokens");
        // TODO: Verification token cleanup implementation
    }

    @Override
    public void recalculateAllProfileCompletions() {
        log.info("Recalculating all profile completion rates");
        
        List<User> allUsers = userRepository.findAll().stream()
            .filter(user -> !user.isDeleted())
            .toList();
        
        allUsers.forEach(user -> {
            int completionRate = calculateProfileCompletionRate(user);
            user.setProfileCompletionRate(completionRate);
        });
        
        userRepository.saveAll(allUsers);
        log.info("Recalculated profile completion for {} users", allUsers.size());
    }

    // === PRIVATE HELPER METHODS ===

    private User findByIdOrThrow(String id) {
        return userRepository.findById(id)
            .filter(user -> !user.isDeleted())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private void validateUserForCreation(User user) {
        if (!StringUtils.hasText(user.getEmail())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Email is required");
        }
        
        if (!StringUtils.hasText(user.getPhone())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Phone is required");
        }
        
        if (!StringUtils.hasText(user.getPassword())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Password is required");
        }
        
        if (!isPasswordStrong(user.getPassword())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Password is not strong enough");
        }
        
        if (user.getRole() == null) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "User role is required");
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
        
        // Kişisel bilgiler
        if (newUser.getGender() != null) {
            existingUser.setGender(newUser.getGender());
        }
        
        if (newUser.getBirthDate() != null) {
            existingUser.setBirthDate(newUser.getBirthDate());
        }
        
        // Lokasyon
        if (StringUtils.hasText(newUser.getProvince())) {
            existingUser.setProvince(newUser.getProvince());
        }
        
        if (StringUtils.hasText(newUser.getDistrict())) {
            existingUser.setDistrict(newUser.getDistrict());
        }
        
        // Tercihler
        if (newUser.getPreferredLanguage() != null) {
            existingUser.setPreferredLanguage(newUser.getPreferredLanguage());
        }
        
        if (newUser.getNotificationPreferences() != null) {
            existingUser.setNotificationPreferences(newUser.getNotificationPreferences());
        }
    }

    private int calculateProfileCompletionRate(User user) {
        int totalFields = 10; // Toplam kontrol edilecek alan sayısı
        int completedFields = 0;
        
        // Zorunlu alanlar
        if (StringUtils.hasText(user.getFirstName())) completedFields++;
        if (StringUtils.hasText(user.getLastName())) completedFields++;
        if (StringUtils.hasText(user.getEmail())) completedFields++;
        if (StringUtils.hasText(user.getPhone())) completedFields++;
        
        // Opsiyonel ama önemli alanlar
        if (user.getGender() != null) completedFields++;
        if (user.getBirthDate() != null) completedFields++;
        if (StringUtils.hasText(user.getProvince())) completedFields++;
        if (StringUtils.hasText(user.getDistrict())) completedFields++;
        if (user.getEmailVerified() != null && user.getEmailVerified()) completedFields++;
        if (user.getPhoneVerified() != null && user.getPhoneVerified()) completedFields++;
        
        return (completedFields * 100) / totalFields;
    }

    private String generateTemporaryPassword() {
        // Geçici şifre oluşturma (gerçek uygulamada daha güvenli olmalı)
        return "TempPass123!";
    }
}