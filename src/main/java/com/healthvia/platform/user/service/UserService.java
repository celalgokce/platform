// user/service/UserService.java
package com.healthvia.platform.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.healthvia.platform.common.enums.Language;
import com.healthvia.platform.common.enums.UserRole;
import com.healthvia.platform.common.enums.UserStatus;
import com.healthvia.platform.user.entity.User;

/**
 * Ana kullanıcı yönetimi servisi - TEMEL İŞLEMLER
 */
public interface UserService {

    // === BASIC CRUD OPERATIONS ===
    
    /**
     * Kullanıcı oluştur
     */
    User createUser(User user);
    
    /**
     * Kullanıcı güncelle
     */
    User updateUser(String id, User user);
    
    /**
     * Kullanıcı bul (ID ile)
     */
    Optional<User> findById(String id);
    
    /**
     * Kullanıcı sil (soft delete)
     */
    void deleteUser(String id, String deletedBy);
    
    /**
     * Tüm kullanıcıları listele (sayfalı)
     */
    Page<User> findAll(Pageable pageable);

    // === AUTHENTICATION CORE METHODS ===
    
    /**
     * Email ile kullanıcı bul - Authentication için
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Email veya telefon ile kullanıcı bul
     */
    Optional<User> findByEmailOrPhone(String emailOrPhone);

    // === ACCOUNT MANAGEMENT ===
    
    /**
     * Hesap durumu değiştir
     */
    User updateUserStatus(String userId, UserStatus status, String updatedBy);
    
    /**
     * Email doğrula
     */
    void verifyEmail(String userId);
    
    /**
     * Telefon doğrula
     */
    void verifyPhone(String userId);

    // === PROFILE MANAGEMENT - HATALARI ÇÖZEN METHODLAR ===
    
    /**
     * Bildirim tercihlerini güncelle
     */
    User updateNotificationPreferences(String userId, List<String> preferences);
    
    /**
     * Avatar güncelle
     */
    User updateAvatar(String userId, String avatarUrl);
    
    /**
     * Dil tercihi güncelle
     */
    User updateLanguagePreference(String userId, Language language);

    // === SEARCH & FILTER METHODS ===
    
    /**
     * Kullanıcı arama
     */
    Page<User> searchUsers(String searchTerm, Pageable pageable);
    
    /**
     * Role göre kullanıcı arama
     */
    Page<User> searchUsersByRole(UserRole role, String searchTerm, Pageable pageable);
    
    /**
     * Role göre kullanıcı listesi
     */
    List<User> findByRole(UserRole role);
    
    /**
     * Status göre kullanıcı listesi
     */
    List<User> findByStatus(UserStatus status);
    
    /**
     * Role ve status göre kullanıcı listesi
     */
    Page<User> findByRoleAndStatus(UserRole role, UserStatus status, Pageable pageable);
    
    /**
     * Lokasyon göre kullanıcı listesi
     */
    List<User> findByLocation(String province, String district);

    // === VALIDATION METHODS ===
    
    /**
     * Email benzersizlik kontrolü
     */
    boolean isEmailAvailable(String email);
    
    /**
     * Telefon benzersizlik kontrolü
     */
    boolean isPhoneAvailable(String phone);
    
    /**
     * Email benzersizlik kontrolü (güncelleme için)
     */
    boolean isEmailAvailableForUpdate(String userId, String email);
    
    /**
     * Telefon benzersizlik kontrolü (güncelleme için)
     */
    boolean isPhoneAvailableForUpdate(String userId, String phone);

    // === ADMIN OPERATIONS ===
    
    /**
     * Kullanıcı hesabını aktifleştir (admin)
     */
    User activateUser(String userId, String activatedBy);
    
    /**
     * Kullanıcı hesabını askıya al (admin)
     */
    User suspendUser(String userId, String reason, String suspendedBy);
    
    /**
     * Kullanıcı hesabını kalıcı sil (admin - hard delete)
     */
    void permanentlyDeleteUser(String userId, String deletedBy);
    
    /**
     * Silinen kullanıcıları geri yükle (admin)
     */
    User restoreDeletedUser(String userId, String restoredBy);
    
    /**
     * Tüm kullanıcıları listele (silinmiş dahil - admin)
     */
    Page<User> findAllIncludingDeleted(Pageable pageable);

    // === MAINTENANCE METHODS ===
    
    /**
     * Süresi dolmuş hesap kilitlerini temizle
     */
    void cleanupExpiredAccountLocks();
    
    /**
     * Eski doğrulama token'ları temizle
     */
    void cleanupExpiredVerificationTokens();

    // === HELPER METHODS ===
    
    /**
     * Kullanıcı var mı kontrolü
     */
    boolean existsById(String userId);
    
    /**
     * Kullanıcı aktif mi kontrolü
     */
    boolean isUserActive(String userId);
    
    /**
     * Kullanıcı silindi mi kontrolü
     */
    boolean isUserDeleted(String userId);
}