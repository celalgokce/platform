// user/service/UserBulkService.java
package com.healthvia.platform.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.healthvia.platform.common.enums.Language;
import com.healthvia.platform.common.enums.UserStatus;
import com.healthvia.platform.user.entity.User;

/**
 * Kullanıcı toplu işlemler servisi
 * - Toplu kullanıcı oluşturma
 * - Toplu güncelleme
 * - Toplu silme
 * - Import/Export işlemleri
 * - Batch işlemler
 */
public interface UserBulkService {

    // === BULK CREATE OPERATIONS ===
    
    /**
     * Toplu kullanıcı oluştur
     */
    BulkOperationResult createUsers(List<User> users);
    
    /**
     * CSV'den kullanıcı import et
     */
    BulkOperationResult importUsersFromCsv(String csvContent);
    
    /**
     * Excel'den kullanıcı import et
     */
    BulkOperationResult importUsersFromExcel(byte[] excelContent);
    
    /**
     * JSON'dan kullanıcı import et
     */
    BulkOperationResult importUsersFromJson(String jsonContent);
    
    /**
     * External system'den kullanıcı import et
     */
    BulkOperationResult importUsersFromExternalSystem(String systemName, Map<String, Object> importParams);

    // === BULK UPDATE OPERATIONS ===
    
    /**
     * Toplu durum güncelleme
     */
    BulkOperationResult updateUsersStatus(List<String> userIds, UserStatus status, String updatedBy);
    
    /**
     * Toplu dil tercihi güncelleme
     */
    BulkOperationResult updateUsersLanguage(List<String> userIds, Language language);
    
    /**
     * Toplu bildirim tercihi güncelleme
     */
    BulkOperationResult updateUsersNotificationPreferences(List<String> userIds, List<String> preferences);
    
    /**
     * Toplu profil alanı güncelleme
     */
    BulkOperationResult updateUsersField(List<String> userIds, String fieldName, Object fieldValue);
    
    /**
     * Criteria göre toplu güncelleme
     */
    BulkOperationResult updateUsersByCriteria(UserCriteria criteria, Map<String, Object> updates);
    
    /**
     * Toplu şifre sıfırlama
     */
    BulkOperationResult resetPasswordsForUsers(List<String> userIds, boolean sendEmail);

    // === BULK DELETE OPERATIONS ===
    
    /**
     * Toplu kullanıcı silme (soft delete)
     */
    BulkOperationResult deleteUsers(List<String> userIds, String deletedBy);
    
    /**
     * Criteria göre toplu silme
     */
    BulkOperationResult deleteUsersByCriteria(UserCriteria criteria, String deletedBy);
    
    /**
     * Toplu kalıcı silme (hard delete)
     */
    BulkOperationResult permanentlyDeleteUsers(List<String> userIds, String deletedBy);
    
    /**
     * Inactive kullanıcıları toplu sil
     */
    BulkOperationResult deleteInactiveUsers(int inactiveDays, String deletedBy);
    
    /**
     * Doğrulanmamış kullanıcıları toplu sil
     */
    BulkOperationResult deleteUnverifiedUsers(int daysSinceRegistration, String deletedBy);

    // === BULK VERIFICATION ===
    
    /**
     * Toplu email doğrulama
     */
    BulkOperationResult verifyEmails(List<String> userIds);
    
    /**
     * Toplu telefon doğrulama
     */
    BulkOperationResult verifyPhones(List<String> userIds);
    
    /**
     * Toplu hesap aktivasyonu
     */
    BulkOperationResult activateUsers(List<String> userIds, String activatedBy);
    
    /**
     * Toplu hesap askıya alma
     */
    BulkOperationResult suspendUsers(List<String> userIds, String reason, String suspendedBy);

    // === BULK EXPORT OPERATIONS ===
    
    /**
     * Kullanıcıları CSV'ye export et
     */
    String exportUsersToCsv(List<String> userIds, List<String> fields);
    
    /**
     * Kullanıcıları Excel'e export et
     */
    byte[] exportUsersToExcel(List<String> userIds, List<String> fields);
    
    /**
     * Kullanıcıları JSON'a export et
     */
    String exportUsersToJson(List<String> userIds, List<String> fields);
    
    /**
     * Criteria göre export
     */
    String exportUsersByCriteria(UserCriteria criteria, ExportFormat format, List<String> fields);
    
    /**
     * Tüm kullanıcıları export et (admin)
     */
    String exportAllUsers(ExportFormat format, List<String> fields);

    // === BULK COMMUNICATION ===
    
    /**
     * Toplu email gönderme
     */
    BulkOperationResult sendBulkEmail(List<String> userIds, String subject, String content);
    
    /**
     * Toplu SMS gönderme
     */
    BulkOperationResult sendBulkSms(List<String> userIds, String message);
    
    /**
     * Toplu push notification
     */
    BulkOperationResult sendBulkPushNotification(List<String> userIds, String title, String message);
    
    /**
     * Criteria göre iletişim
     */
    BulkOperationResult sendCommunicationByCriteria(UserCriteria criteria, CommunicationType type, CommunicationContent content);

    // === BULK ANALYSIS ===
    
    /**
     * Toplu profil tamamlanma oranı hesaplama
     */
    BulkOperationResult recalculateProfileCompletions(List<String> userIds);
    
    /**
     * Toplu data validation
     */
    ValidationReport validateUsersData(List<String> userIds);
    
    /**
     * Duplicate kullanıcı tespiti
     */
    DuplicateReport findDuplicateUsers(DuplicateCriteria criteria);
    
    /**
     * Toplu data cleaning
     */
    BulkOperationResult cleanUsersData(List<String> userIds, List<CleaningRule> rules);

    // === BATCH PROCESSING ===
    
    /**
     * Batch job başlat
     */
    String startBatchJob(BatchJobType jobType, Map<String, Object> parameters);
    
    /**
     * Batch job durumu kontrol et
     */
    BatchJobStatus getBatchJobStatus(String jobId);
    
    /**
     * Batch job iptal et
     */
    void cancelBatchJob(String jobId);
    
    /**
     * Aktif batch job'ları listele
     */
    List<BatchJobInfo> getActiveBatchJobs();

    // === MIGRATION OPERATIONS ===
    
    /**
     * Kullanıcı verilerini migrate et
     */
    BulkOperationResult migrateUsersData(String fromVersion, String toVersion);
    
    /**
     * Schema migration
     */
    BulkOperationResult migrateUsersSchema(List<String> userIds, SchemaMapping mapping);
    
    /**
     * Data transformation
     */
    BulkOperationResult transformUsersData(List<String> userIds, List<TransformationRule> rules);

    // === BULK UTILITIES ===
    
    /**
     * Bulk operation önizleme
     */
    BulkOperationPreview previewBulkOperation(BulkOperationType operationType, Map<String, Object> parameters);
    
    /**
     * Bulk operation geri alma
     */
    BulkOperationResult rollbackBulkOperation(String operationId);
    
    /**
     * Bulk operation geçmişi
     */
    List<BulkOperationHistory> getBulkOperationHistory(String performedBy, int limit);

    // === NESTED ENUMS & CLASSES ===
    
    enum ExportFormat {
        CSV, EXCEL, JSON, XML
    }
    
    enum CommunicationType {
        EMAIL, SMS, PUSH_NOTIFICATION, IN_APP_NOTIFICATION
    }
    
    enum BatchJobType {
        BULK_CREATE, BULK_UPDATE, BULK_DELETE, BULK_EXPORT, BULK_COMMUNICATION, DATA_MIGRATION
    }
    
    enum BatchJobStatus {
        PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
    }
    
    enum BulkOperationType {
        CREATE, UPDATE, DELETE, EXPORT, COMMUNICATE, VERIFY, ACTIVATE, SUSPEND
    }
    
    class BulkOperationResult {
        private String operationId;
        private BulkOperationType operationType;
        private int totalRecords;
        private int successCount;
        private int failureCount;
        private int skippedCount;
        private List<String> errors;
        private List<String> warnings;
        private Map<String, Object> summary;
        private boolean completed;
        private long durationMs;
        
        public String getOperationId() {
            return operationId;
        }

        // getters and setters
    }
    
    class UserCriteria {
        private List<UserStatus> statuses;
        private List<String> roles;
        private String province;
        private String district;
        private Boolean emailVerified;
        private Boolean phoneVerified;
        private Integer minProfileCompletion;
        private Integer maxProfileCompletion;
        private Integer inactiveDays;
        private Map<String, Object> customCriteria;
        
        // getters and setters
    }
    
    class CommunicationContent {
        private String subject;
        private String content;
        private String template;
        private Map<String, Object> variables;
        private List<String> attachments;
        
        // getters and setters
    }
    
    class ValidationReport {
        private int totalUsers;
        private int validUsers;
        private int invalidUsers;
        private Map<String, List<String>> validationErrors;
        private List<String> suggestions;
        
        // getters and setters
    }
    
    class DuplicateReport {
        private int totalDuplicates;
        private Map<String, List<String>> duplicateGroups;
        private List<DuplicateMatch> matches;
        
        // getters and setters
    }
    
    class DuplicateCriteria {
        private boolean checkEmail;
        private boolean checkPhone;
        private boolean checkName;
        private boolean checkTcKimlik;
        private double similarityThreshold;
        
        // getters and setters
    }
    
    class DuplicateMatch {
        private List<String> userIds;
        private String matchType;
        private double similarity;
        private Map<String, Object> commonFields;
        
        // getters and setters
    }
    
    class CleaningRule {
        private String fieldName;
        private String action; // NORMALIZE, TRIM, REMOVE_SPECIAL_CHARS, etc.
        private Map<String, Object> parameters;
        
        // getters and setters
    }
    
    class BatchJobInfo {
        private String jobId;
        private BatchJobType jobType;
        private BatchJobStatus status;
        private int progress;
        private String startedBy;
        private LocalDateTime startTime;
        private LocalDateTime estimatedEndTime;
        private Map<String, Object> parameters;
        
        // getters and setters
    }
    
    class BulkOperationPreview {
        private int estimatedAffectedRecords;
        private List<String> sampleAffectedUsers;
        private List<String> potentialIssues;
        private long estimatedDurationMs;
        private Map<String, Object> previewData;
        
        // getters and setters
    }
    
    class BulkOperationHistory {
        private String operationId;
        private BulkOperationType operationType;
        private String performedBy;
        private LocalDateTime performedAt;
        private BulkOperationResult result;
        private boolean canRollback;
        
        // getters and setters
    }
    
    class SchemaMapping {
        private Map<String, String> fieldMappings;
        private Map<String, Object> defaultValues;
        private List<String> fieldsToRemove;
        
        // getters and setters
    }
    
    class TransformationRule {
        private String fieldName;
        private String transformation;
        private Map<String, Object> parameters;
        
        // getters and setters
    }
}