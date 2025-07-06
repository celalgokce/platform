// user/service/UserProfileService.java
package com.healthvia.platform.user.service;

import java.util.List;

import com.healthvia.platform.common.enums.Language;
import com.healthvia.platform.user.entity.User;

/**
 * Kullanıcı profil yönetimi servisi
 * - Profil tamamlama oranı hesaplama
 * - Avatar yönetimi  
 * - Dil ve bildirim tercihleri
 */
public interface UserProfileService {

    /**
     * Profil tamamlanma oranını hesapla ve güncelle
     */
    int calculateAndUpdateProfileCompletion(String userId);
    
    /**
     * Profil tamamlanma oranını hesapla (kaydetmeden)
     */
    int calculateProfileCompletionRate(User user);
    
    /**
     * Avatar güncelle
     */
    User updateAvatar(String userId, String avatarUrl);
    
    /**
     * Avatar sil
     */
    User removeAvatar(String userId);
    
    /**
     * Dil tercihi güncelle
     */
    User updateLanguagePreference(String userId, Language language);
    
    /**
     * Bildirim tercihlerini güncelle
     */
    User updateNotificationPreferences(String userId, List<String> preferences);
    
    /**
     * Bildirim tercihi ekle
     */
    User addNotificationPreference(String userId, String preference);
    
    /**
     * Bildirim tercihi kaldır
     */
    User removeNotificationPreference(String userId, String preference);
    
    /**
     * Profil verilerini dışa aktar
     */
    String exportProfileData(String userId);
    
    /**
     * Profil tamamlanma önerilerini getir
     */
    List<String> getProfileCompletionSuggestions(String userId);
    
    /**
     * Tüm kullanıcıların profil tamamlanma oranını yeniden hesapla
     */
    void recalculateAllProfileCompletions();
}