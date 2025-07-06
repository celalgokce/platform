// user/service/UserSecurityService.java
package com.healthvia.platform.user.service;

import java.time.LocalDateTime;
import java.util.List;

import com.healthvia.platform.user.entity.User;

/**
 * Kullanıcı güvenlik yönetimi servisi
 * - Şifre yönetimi
 * - Hesap kilitleme/açma
 * - Başarısız giriş denemeleri
 * - Güvenlik olayları
 */
public interface UserSecurityService {

    // === PASSWORD MANAGEMENT ===
    
    /**
     * Şifre doğrulama
     */
    boolean validatePassword(String userId, String rawPassword);
    
    /**
     * Şifre değiştir
     */
    void changePassword(String userId, String oldPassword, String newPassword);
    
    /**
     * Şifre sıfırlama (email ile)
     */
    void resetPassword(String email);
    
    /**
     * Şifre sıfırlama token'ı oluştur
     */
    String generatePasswordResetToken(String userId);
    
    /**
     * Şifre sıfırlama token'ı doğrula
     */
    boolean validatePasswordResetToken(String token);
    
    /**
     * Token ile şifre sıfırla
     */
    void resetPasswordWithToken(String token, String newPassword);
    
    /**
     * Şifre kuvvetlilik kontrolü
     */
    boolean isPasswordStrong(String password);
    
    /**
     * Geçici şifre oluştur
     */
    String generateTemporaryPassword();

    // === ACCOUNT LOCKING ===
    
    /**
     * Hesap kilitle
     */
    void lockAccount(String userId, int durationMinutes, String reason);
    
    /**
     * Hesap kilidini aç
     */
    void unlockAccount(String userId);
    
    /**
     * Hesap kilitli mi kontrol et
     */
    boolean isAccountLocked(String userId);
    
    /**
     * Kilitli hesapları bul
     */
    List<User> findLockedAccounts();
    
    /**
     * Süresi dolmuş hesap kilitlerini temizle
     */
    void cleanupExpiredAccountLocks();

    // === FAILED LOGIN ATTEMPTS ===
    
    /**
     * Başarısız giriş denemesi kaydet
     */
    void recordFailedLoginAttempt(String userId);
    
    /**
     * Başarısız giriş denemelerini sıfırla
     */
    void resetFailedLoginAttempts(String userId);
    
    /**
     * Başarısız girişi fazla olan kullanıcılar
     */
    List<User> findUsersWithFailedAttempts(int minFailedAttempts);
    
    /**
     * Son giriş tarihini güncelle
     */
    void updateLastLogin(String userId);

    // === SECURITY EVENTS ===
    
    /**
     * Güvenlik olayı kaydet
     */
    void logSecurityEvent(String userId, String eventType, String description);
    
    /**
     * Şüpheli aktivite tespit et
     */
    boolean detectSuspiciousActivity(String userId);
    
    /**
     * Güvenlik uyarısı gönder
     */
    void sendSecurityAlert(String userId, String alertType, String message);

    // === TWO FACTOR AUTHENTICATION ===
    
    /**
     * 2FA aktifleştir
     */
    String enable2FA(String userId);
    
    /**
     * 2FA deaktifleştir
     */
    void disable2FA(String userId);
    
    /**
     * 2FA kodu doğrula
     */
    boolean verify2FACode(String userId, String code);
    
    /**
     * 2FA backup kodları oluştur
     */
    List<String> generate2FABackupCodes(String userId);

    // === SESSION MANAGEMENT ===
    
    /**
     * Aktif oturumları sonlandır
     */
    void terminateAllSessions(String userId);
    
    /**
     * Belirli cihazın oturumunu sonlandır
     */
    void terminateDeviceSession(String userId, String deviceId);
    
    /**
     * Aktif oturum sayısını getir
     */
    int getActiveSessionCount(String userId);

    // === SECURITY ANALYTICS ===
    
    /**
     * Güvenlik risk skoru hesapla
     */
    int calculateSecurityRiskScore(String userId);
    
    /**
     * Risk altındaki kullanıcıları bul
     */
    List<User> findHighRiskUsers();
    
    /**
     * Güvenlik metrikleri getir
     */
    SecurityMetrics getSecurityMetrics(String userId);
    
    /**
     * Güvenlik raporu oluştur
     */
    String generateSecurityReport(String userId, LocalDateTime fromDate, LocalDateTime toDate);

    // === NESTED CLASSES ===
    
    class SecurityMetrics {
        private int totalLoginAttempts;
        private int failedLoginAttempts;
        private int successfulLogins;
        private LocalDateTime lastLogin;
        private LocalDateTime lastFailedLogin;
        private boolean is2FAEnabled;
        private int riskScore;
        
        // getters and setters
    }
}