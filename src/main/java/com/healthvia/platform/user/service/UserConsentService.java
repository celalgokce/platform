// user/service/UserConsentService.java
package com.healthvia.platform.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.healthvia.platform.user.entity.User;

/**
 * Kullanıcı onay ve GDPR yönetimi servisi
 * - GDPR onayları
 * - Veri işleme onayları
 * - Marketing onayları
 * - Veri dışa aktarma
 * - Unutulma hakkı
 */
public interface UserConsentService {

    // === GDPR CONSENT MANAGEMENT ===
    
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
     * Tüm onayları toplu güncelle
     */
    void updateAllConsents(String userId, Map<String, Boolean> consents);
    
    /**
     * Onay geçmişini getir
     */
    List<ConsentHistory> getConsentHistory(String userId);

    // === CONSENT QUERIES ===
    
    /**
     * GDPR onayı verilmemiş kullanıcılar
     */
    List<User> findUsersWithoutGdprConsent();
    
    /**
     * Veri işleme onayı verilmemiş kullanıcılar
     */
    List<User> findUsersWithoutDataProcessingConsent();
    
    /**
     * Marketing onayı vermiş kullanıcılar
     */
    List<User> findUsersWithMarketingConsent();
    
    /**
     * Belirli onay türüne sahip kullanıcılar
     */
    List<User> findUsersByConsentType(ConsentType consentType, boolean hasConsent);
    
    /**
     * Onay durumu eksik kullanıcılar
     */
    List<User> findUsersWithIncompleteConsents();

    // === DATA EXPORT (GDPR Article 20) ===
    
    /**
     * Kullanıcı verilerini dışa aktar (JSON)
     */
    String exportUserDataAsJson(String userId);
    
    /**
     * Kullanıcı verilerini dışa aktar (XML)
     */
    String exportUserDataAsXml(String userId);
    
    /**
     * Kullanıcı verilerini dışa aktar (CSV)
     */
    String exportUserDataAsCsv(String userId);
    
    /**
     * Veri dışa aktarma talebinde bulun
     */
    String requestDataExport(String userId, ExportFormat format);
    
    /**
     * Dışa aktarma talebinin durumunu kontrol et
     */
    ExportStatus checkExportStatus(String requestId);

    // === RIGHT TO BE FORGOTTEN (GDPR Article 17) ===
    
    /**
     * Kullanıcı verilerini anonimleştir
     */
    void anonymizeUserData(String userId, String reason);
    
    /**
     * Kullanıcı verilerini tamamen sil
     */
    void permanentlyDeleteUserData(String userId, String reason);
    
    /**
     * Silme talebinde bulun
     */
    String requestDataDeletion(String userId, String reason);
    
    /**
     * Silme talebinin durumunu kontrol et
     */
    DeletionStatus checkDeletionStatus(String requestId);
    
    /**
     * Silinecek veri türlerini listele
     */
    List<String> getDataTypesForDeletion(String userId);

    // === CONSENT VALIDATION ===
    
    /**
     * Kullanıcının belirli işlem için onayı var mı
     */
    boolean hasConsentForOperation(String userId, String operation);
    
    /**
     * Marketing işlemleri için onay kontrolü
     */
    boolean canSendMarketingCommunication(String userId);
    
    /**
     * Veri analizi için onay kontrolü
     */
    boolean canUseDataForAnalytics(String userId);
    
    /**
     * Üçüncü taraf paylaşım onayı kontrolü
     */
    boolean canShareDataWithThirdParties(String userId);
    
    /**
     * Onay geçerlilik süresi kontrolü
     */
    boolean isConsentValid(String userId, ConsentType consentType);

    // === CONSENT NOTIFICATIONS ===
    
    /**
     * Onay yenileme hatırlatması gönder
     */
    void sendConsentRenewalReminder(String userId);
    
    /**
     * Onay değişikliği bildirimi gönder
     */
    void notifyConsentChange(String userId, ConsentType consentType, boolean newStatus);
    
    /**
     * GDPR hakları hakkında bilgilendirme gönder
     */
    void sendGdprRightsInformation(String userId);

    // === COMPLIANCE REPORTING ===
    
    /**
     * GDPR uyumluluk raporu oluştur
     */
    GdprComplianceReport generateComplianceReport(LocalDateTime fromDate, LocalDateTime toDate);
    
    /**
     * Onay istatistikleri getir
     */
    ConsentStatistics getConsentStatistics();
    
    /**
     * Veri işleme aktivitesi raporu
     */
    String generateDataProcessingReport(String userId);

    // === AUDIT TRAIL ===
    
    /**
     * Onay değişikliği audit kaydı oluştur
     */
    void createConsentAuditRecord(String userId, ConsentType consentType, 
                                  boolean oldValue, boolean newValue, String reason);
    
    /**
     * Veri erişim audit kaydı oluştur
     */
    void createDataAccessAuditRecord(String userId, String accessType, 
                                     String accessedBy, String purpose);
    
    /**
     * Audit kayıtlarını getir
     */
    List<AuditRecord> getAuditRecords(String userId, LocalDateTime fromDate, LocalDateTime toDate);

    // === NESTED ENUMS & CLASSES ===
    
    enum ConsentType {
        GDPR_CONSENT,
        DATA_PROCESSING,
        MARKETING,
        DATA_SHARING,
        COOKIES,
        ANALYTICS,
        THIRD_PARTY_SHARING
    }
    
    enum ExportFormat {
        JSON, XML, CSV, PDF
    }
    
    enum ExportStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }
    
    enum DeletionStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED, REJECTED
    }
    
    class ConsentHistory {
        private ConsentType consentType;
        private boolean value;
        private LocalDateTime timestamp;
        private String updatedBy;
        private String reason;
        
        // getters and setters
    }
    
    class GdprComplianceReport {
        private long totalUsers;
        private long usersWithGdprConsent;
        private long usersWithDataProcessingConsent;
        private long usersWithMarketingConsent;
        private long dataExportRequests;
        private long dataDeletionRequests;
        private double compliancePercentage;
        
        // getters and setters
    }
    
    class ConsentStatistics {
        private Map<ConsentType, Long> consentCounts;
        private Map<ConsentType, Double> consentPercentages;
        private LocalDateTime lastUpdated;
        
        // getters and setters
    }
    
    class AuditRecord {
        private String userId;
        private String action;
        private String details;
        private LocalDateTime timestamp;
        private String performedBy;
        
        // getters and setters
    }
}