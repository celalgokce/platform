package com.healthvia.platform.user.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.healthvia.platform.common.constants.ErrorCodes;
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

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$"
    );

    @Override
    public User createUser(User user) {
        validateUserForCreation(user);
        if (!isEmailAvailable(user.getEmail())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Email already exists");
        }
        if (!isPhoneAvailable(user.getPhone())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Phone already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.PENDING_VERIFICATION);
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(String id, User user) {
        User existingUser = findByIdOrThrow(id);
        if (!existingUser.getEmail().equals(user.getEmail()) && !isEmailAvailableForUpdate(id, user.getEmail())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Email already exists");
        }
        if (!existingUser.getPhone().equals(user.getPhone()) && !isPhoneAvailableForUpdate(id, user.getPhone())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Phone already exists");
        }
        updateUserFields(existingUser, user);
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(String id) {
        return userRepository.findById(id).filter(user -> !user.isDeleted());
    }

    @Override
    public void deleteUser(String id, String deletedBy) {
        User user = findByIdOrThrow(id);
        user.markAsDeleted(deletedBy);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

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
    public User updateUserStatus(String userId, UserStatus status, String updatedBy) {
        User user = findByIdOrThrow(userId);
        user.setStatus(status);
        return userRepository.save(user);
    }

    @Override
    public void verifyEmail(String userId) {
        User user = findByIdOrThrow(userId);
        user.setEmailVerified(true);
        if (UserStatus.PENDING_VERIFICATION.equals(user.getStatus())) {
            user.setStatus(UserStatus.ACTIVE);
        }
        userRepository.save(user);
    }

    @Override
    public void verifyPhone(String userId) {
        User user = findByIdOrThrow(userId);
        user.setPhoneVerified(true);
        userRepository.save(user);
    }

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
    public User activateUser(String userId, String activatedBy) {
        return updateUserStatus(userId, UserStatus.ACTIVE, activatedBy);
    }

    @Override
    public User suspendUser(String userId, String reason, String suspendedBy) {
        return updateUserStatus(userId, UserStatus.SUSPENDED, suspendedBy);
    }

    @Override
    public void permanentlyDeleteUser(String userId, String deletedBy) {
        User user = findByIdOrThrow(userId);
        userRepository.delete(user);
    }

    @Override
    public User restoreDeletedUser(String userId, String restoredBy) {
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
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAllIncludingDeleted(Pageable pageable) {
        return userRepository.findAllIncludingDeleted(pageable);
    }

    @Override
    public void cleanupExpiredAccountLocks() {
        List<User> lockedUsers = userRepository.findLockedUsers(LocalDateTime.now());
        lockedUsers.stream()
            .filter(user -> user.getAccountLockedUntil() != null && user.getAccountLockedUntil().isBefore(LocalDateTime.now()))
            .forEach(user -> {
                user.setAccountLockedUntil(null);
                user.resetFailedLoginAttempts();
            });
        if (!lockedUsers.isEmpty()) {
            userRepository.saveAll(lockedUsers);
        }
    }

    private User findByIdOrThrow(String id) {
        return userRepository.findById(id)
            .filter(user -> !user.isDeleted())
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private void validateUserForCreation(User user) {
        if (!StringUtils.hasText(user.getEmail()) || !StringUtils.hasText(user.getPhone()) || !StringUtils.hasText(user.getPassword()) || user.getRole() == null) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Required fields are missing");
        }
        if (!isPasswordStrong(user.getPassword())) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Password is not strong enough");
        }
    }

    private void updateUserFields(User existingUser, User newUser) {
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
    }

    private boolean isPasswordStrong(String password) {
        return StringUtils.hasText(password) && PASSWORD_PATTERN.matcher(password).matches();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserDeleted(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(User::isDeleted).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserActive(String userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(user -> UserStatus.ACTIVE.equals(user.getStatus()) && !user.isDeleted()).orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public void cleanupExpiredVerificationTokens() {
        // Implement logic to cleanup expired verification tokens if applicable.
        // If not needed, leave empty or log a message.
        log.info("cleanupExpiredVerificationTokens called, but not implemented.");
    }
}
