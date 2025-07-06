// user/service/UserService.java
package com.healthvia.platform.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.healthvia.platform.common.enums.Language;
import com.healthvia.platform.common.enums.UserRole;
import com.healthvia.platform.common.enums.UserStatus;
import com.healthvia.platform.user.entity.User;

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

    // === AUTHENTICATION METHODS ===
    
    /**
     * Email ile kullanıcı bul - Authentication için
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Email veya telefon ile kullanıcı bul
     */
    Optional<User> findByEmailOrPhone(String emailOrPhone);
    
    /**
     * Kullanıcı şifre doğrulama
     */
    boolean validatePassword(String userId, String rawPassword);
    
    /**
     * Şifre değiştir
     */
    void changePassword(String userId, String oldPassword, String newPassword);
    
    /**
     * Şifre sıfırlama
     */
    void resetPassword(String email);
    
    /**
     * Son giriş tarihini güncelle
     */
    void updateLastLogin(String userId);

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
    
    /**
     * Hesap kilitle
     */
    void lockAccount(String userId, int durationMinutes, String reason);
    
    /**
     * Hesap kilidini aç
     */
    void unlockAccount(String userId);
    
    /**
     * Başarısız giriş denemesi kaydet
     */
    void recordFailedLoginAttempt(String userId);
    
    /**
     * Başarısız giriş denemelerini sıfırla
     */
    void resetFailedLoginAttempts(String userId);

    // === PROFILE MANAGEMENT ===
    
    /**
     * Profil tamamlanma oranını hesapla ve güncelle
     */
    int calculateAndUpdateProfileCompletion(String userId);
    
    /**
     * Avatar güncelle
     */
    User updateAvatar(String userId, String avatarUrl);
    
    /**
     * Dil tercihi güncelle
     */
    User updateLanguagePreference(String userId, Language language);
    
    /**
     * Bildirim tercihlerini güncelle
     */
    User updateNotificationPreferences(String userId, List<String> preferences);

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
    
    /**
     * Şifre kuvvetlilik kontrolü
     */
    boolean isPasswordStrong(String password);

    // === GDPR & CONSENT METHODS ===
    
    /**
     * GDPR onayı güncelle
     */
    void updateGdprConsent(String userId, boolean consent);
    
    /**
     * Veri işleme onayı güncelle
     */
    void updateDataProcessingConsent(String userId, boolean consent);
    
    /**
     * Marketing onayı güncelle
     */
    void updateMarketingConsent(String userId, boolean consent);
    
    /**
     * Veri paylaşım onayı güncelle
     */
    void updateDataSharingConsent(String userId, boolean consent);
    
    /**
     * GDPR onayı verilmemiş kullanıcılar
     */
    List<User> findUsersWithoutGdprConsent();

    // === ANALYTICS & STATISTICS ===
    
    /**
     * Role göre kullanıcı sayısı
     */
    long countByRole(UserRole role);
    
    /**
     * Status göre kullanıcı sayısı
     */
    long countByStatus(UserStatus status);
    
    /**
     * Email doğrulanmamış kullanıcı sayısı
     */
    long countUnverifiedEmails();
    
    /**
     * Belirli tarih aralığında kayıt olan kullanıcı sayısı
     */
    long countUsersRegisteredBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Aktif olmayan kullanıcılar (son giriş tarihi eski)
     */
    List<User> findInactiveUsers(LocalDateTime lastLoginBefore);
    
    /**
     * Hiç giriş yapmamış kullanıcılar
     */
    List<User> findNeverLoggedInUsers(LocalDateTime createdBefore);

    // === BULK OPERATIONS ===
    
    /**
     * Toplu kullanıcı oluştur
     */
    List<User> createUsers(List<User> users);
    
    /**
     * Toplu durum güncelleme
     */
    void updateUsersStatus(List<String> userIds, UserStatus status, String updatedBy);
    
    /**
     * Toplu silme
     */
    void deleteUsers(List<String> userIds, String deletedBy);

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

    // === NOTIFICATION METHODS ===
    
    /**
     * Marketing onayı vermiş kullanıcılar
     */
    List<User> findUsersWithMarketingConsent();
    
    /**
     * Dil tercihi göre kullanıcılar
     */
    List<User> findUsersByLanguage(Language language);
    
    /**
     * Belirli bildirim tercihine sahip kullanıcılar
     */
    List<User> findUsersByNotificationPreference(String preference);

    // === SECURITY METHODS ===
    
    /**
     * Kilitli hesapları bul
     */
    List<User> findLockedAccounts();
    
    /**
     * Başarısız girişi fazla olan kullanıcılar
     */
    List<User> findUsersWithFailedAttempts(int minFailedAttempts);
    
    /**
     * Güvenlik olayı kaydet
     */
    void logSecurityEvent(String userId, String eventType, String description);

    // === MAINTENANCE METHODS ===
    
    /**
     * Hesap kilitleri temizle (süresi dolmuş)
     */
    void cleanupExpiredAccountLocks();
    
    /**
     * Eski doğrulama token'ları temizle
     */
    void cleanupExpiredVerificationTokens();
    
    /**
     * Profil tamamlanma oranlarını yeniden hesapla
     */
    void recalculateAllProfileCompletions();
}