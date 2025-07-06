// user/service/UserStatisticsService.java
package com.healthvia.platform.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.healthvia.platform.common.enums.Language;
import com.healthvia.platform.common.enums.UserRole;
import com.healthvia.platform.common.enums.UserStatus;
import com.healthvia.platform.user.entity.User;

/**
 * Kullanıcı istatistik ve analiz servisi
 * - Kullanıcı sayımları
 * - Demografik analizler
 * - Aktivite raporları
 * - Trend analizleri
 */
public interface UserStatisticsService {

    // === BASIC COUNTS ===
    
    /**
     * Role göre kullanıcı sayısı
     */
    long countByRole(UserRole role);
    
    /**
     * Status göre kullanıcı sayısı
     */
    long countByStatus(UserStatus status);
    
    /**
     * Role ve status göre kullanıcı sayısı
     */
    long countByRoleAndStatus(UserRole role, UserStatus status);
    
    /**
     * Aktif kullanıcı sayısı
     */
    long countActiveUsers();
    
    /**
     * Toplam kullanıcı sayısı
     */
    long countTotalUsers();
    
    /**
     * Email doğrulanmamış kullanıcı sayısı
     */
    long countUnverifiedEmails();
    
    /**
     * Telefon doğrulanmamış kullanıcı sayısı
     */
    long countUnverifiedPhones();

    // === TIME BASED STATISTICS ===
    
    /**
     * Belirli tarih aralığında kayıt olan kullanıcı sayısı
     */
    long countUsersRegisteredBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Bu ay kayıt olan kullanıcı sayısı
     */
    long countUsersRegisteredThisMonth();
    
    /**
     * Bu hafta kayıt olan kullanıcı sayısı
     */
    long countUsersRegisteredThisWeek();
    
    /**
     * Bugün kayıt olan kullanıcı sayısı
     */
    long countUsersRegisteredToday();
    
    /**
     * Günlük kayıt istatistikleri (son 30 gün)
     */
    Map<String, Long> getDailyRegistrationStats(int days);
    
    /**
     * Aylık kayıt istatistikleri (son 12 ay)
     */
    Map<String, Long> getMonthlyRegistrationStats(int months);

    // === DEMOGRAPHIC STATISTICS ===
    
    /**
     * Cinsiyet dağılımı
     */
    Map<String, Long> getGenderDistribution();
    
    /**
     * Yaş grubu dağılımı
     */
    Map<String, Long> getAgeGroupDistribution();
    
    /**
     * İl bazında kullanıcı dağılımı
     */
    Map<String, Long> getProvinceDistribution();
    
    /**
     * Dil tercihi dağılımı
     */
    Map<Language, Long> getLanguageDistribution();
    
    /**
     * Role bazında demografik analiz
     */
    DemographicAnalysis getDemographicAnalysisByRole(UserRole role);

    // === ACTIVITY STATISTICS ===
    
    /**
     * Son 30 gün içinde giriş yapan kullanıcı sayısı
     */
    long countActiveUsersInLastDays(int days);
    
    /**
     * Hiç giriş yapmamış kullanıcı sayısı
     */
    long countNeverLoggedInUsers();
    
    /**
     * Belirli süre giriş yapmamış kullanıcılar
     */
    List<User> findInactiveUsers(LocalDateTime lastLoginBefore);
    
    /**
     * En aktif kullanıcılar (son giriş tarihine göre)
     */
    Page<User> findMostActiveUsers(Pageable pageable);
    
    /**
     * Günlük aktif kullanıcı sayısı (son 30 gün)
     */
    Map<String, Long> getDailyActiveUserStats(int days);

    // === PROFILE COMPLETION STATISTICS ===
    
    /**
     * Ortalama profil tamamlanma oranı
     */
    double getAverageProfileCompletionRate();
    
    /**
     * Profil tamamlanma oranı dağılımı
     */
    Map<String, Long> getProfileCompletionDistribution();
    
    /**
     * Düşük profil tamamlanma oranına sahip kullanıcı sayısı
     */
    long countLowProfileCompletionUsers(int maxCompletionRate);
    
    /**
     * Role göre ortalama profil tamamlanma oranı
     */
    Map<UserRole, Double> getAverageProfileCompletionByRole();

    // === VERIFICATION STATISTICS ===
    
    /**
     * Doğrulama istatistikleri
     */
    VerificationStatistics getVerificationStatistics();
    
    /**
     * Role göre doğrulama durumu
     */
    Map<UserRole, VerificationStatistics> getVerificationStatisticsByRole();

    // === LOCATION STATISTICS ===
    
    /**
     * Lokasyon bazında kullanıcı sayısı
     */
    long countUsersByLocation(String province, String district);
    
    /**
     * En popüler şehirler
     */
    List<LocationStatistic> getTopCities(int limit);
    
    /**
     * Bölge bazında kullanıcı dağılımı
     */
    Map<String, Long> getRegionalDistribution();

    // === GROWTH ANALYTICS ===
    
    /**
     * Kullanıcı büyüme oranı (aylık)
     */
    double getMonthlyGrowthRate();
    
    /**
     * Kullanıcı büyüme trendi (son 12 ay)
     */
    List<GrowthTrendData> getGrowthTrend(int months);
    
    /**
     * Role bazında büyüme analizi
     */
    Map<UserRole, Double> getGrowthRateByRole();
    
    /**
     * Kullanıcı churn oranı
     */
    double getChurnRate(int periodDays);

    // === NOTIFICATION STATISTICS ===
    
    /**
     * Bildirim tercih istatistikleri
     */
    Map<String, Long> getNotificationPreferenceStats();
    
    /**
     * Marketing onayı vermiş kullanıcı sayısı
     */
    long countUsersWithMarketingConsent();
    
    /**
     * Belirli bildirim tercihine sahip kullanıcı sayısı
     */
    long countUsersByNotificationPreference(String preference);

    // === ADVANCED ANALYTICS ===
    
    /**
     * Kullanıcı segment analizi
     */
    List<UserSegment> getUserSegments();
    
    /**
     * Kohort analizi
     */
    CohortAnalysisResult getCohortAnalysis(LocalDateTime startDate, int periodDays);
    
    /**
     * Kullanıcı yaşam döngüsü analizi
     */
    UserLifecycleAnalysis getUserLifecycleAnalysis();
    
    /**
     * Retention oranı hesapla
     */
    Map<String, Double> getRetentionRates();

    // === REPORTING ===
    
    /**
     * Kapsamlı kullanıcı raporu oluştur
     */
    ComprehensiveUserReport generateComprehensiveReport(LocalDateTime fromDate, LocalDateTime toDate);
    
    /**
     * Özet dashboard verileri
     */
    UserDashboardData getDashboardData();
    
    /**
     * Trend raporu oluştur
     */
    String generateTrendReport(int months);

    // === NESTED CLASSES ===
    
    class DemographicAnalysis {
        private Map<String, Long> genderDistribution;
        private Map<String, Long> ageGroupDistribution;
        private Map<String, Long> locationDistribution;
        private double averageAge;
        private String mostCommonGender;
        private String mostCommonLocation;
        
        // getters and setters
    }
    
    class VerificationStatistics {
        private long totalUsers;
        private long emailVerified;
        private long phoneVerified;
        private long bothVerified;
        private long noneVerified;
        private double emailVerificationRate;
        private double phoneVerificationRate;
        
        // getters and setters
    }
    
    class LocationStatistic {
        private String city;
        private String province;
        private long userCount;
        private double percentage;
        
        // getters and setters
    }
    
    class GrowthTrendData {
        private String period;
        private long newUsers;
        private long totalUsers;
        private double growthRate;
        private double growthPercentage;
        
        // getters and setters
    }
    
    class UserSegment {
        private String segmentName;
        private String criteria;
        private long userCount;
        private double percentage;
        private Map<String, Object> characteristics;
        
        // getters and setters
    }
    
    class CohortAnalysisResult {
        private Map<String, Map<String, Double>> cohortData;
        private Map<String, Double> retentionRates;
        private LocalDateTime analysisDate;
        
        // getters and setters
    }
    
    class UserLifecycleAnalysis {
        private long newUsers;
        private long activeUsers;
        private long inactiveUsers;
        private long churned;
        private Map<String, Double> transitionRates;
        
        // getters and setters
    }
    
    class ComprehensiveUserReport {
        private UserDashboardData summary;
        private DemographicAnalysis demographics;
        private VerificationStatistics verification;
        private Map<String, Long> registrationTrend;
        private Map<String, Long> activityTrend;
        private List<String> insights;
        private List<String> recommendations;
        
        // getters and setters
    }
    
    class UserDashboardData {
        private long totalUsers;
        private long activeUsers;
        private long newUsersThisMonth;
        private long newUsersToday;
        private double growthRate;
        private double emailVerificationRate;
        private double profileCompletionRate;
        private Map<UserRole, Long> usersByRole;
        private Map<UserStatus, Long> usersByStatus;
        
        // getters and setters
    }
}