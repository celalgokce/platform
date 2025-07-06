// user/entity/Admin.java
package com.healthvia.platform.user.entity;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "admins")
public class Admin extends User {

    // === ADMIN ÖZELLİKLERİ (Minimal) ===
    
    @NotBlank(message = "Departman bilgisi boş olamaz")
    @Size(max = 100, message = "Departman adı en fazla 100 karakter olabilir")
    private String department; // IT, Sağlık Yönetimi, Mali İşler vs.

    @Field("job_title")
    @Size(max = 100, message = "Ünvan en fazla 100 karakter olabilir")
    private String jobTitle; // Sistem Yöneticisi, Platform Müdürü vs.

    @Field("admin_level")
    private AdminLevel adminLevel;

    // === YETKİLER ===
    @Field("permissions")
    private Set<AdminPermission> permissions;

    @Field("can_manage_users")
    private Boolean canManageUsers;

    @Field("can_manage_doctors")
    private Boolean canManageDoctors;

    @Field("can_manage_clinics")
    private Boolean canManageClinics;

    @Field("can_view_reports")
    private Boolean canViewReports;

    @Field("can_manage_system")
    private Boolean canManageSystem;

    // === EMPLOYEE BİLGİLERİ ===
    @Field("employee_id")
    private String employeeId; // Çalışan numarası

    @Field("hire_date")
    private LocalDateTime hireDate;

    @Field("supervisor_id")
    private String supervisorId; // Yönetici ID'si

    // === ACTIVITY TRACKING ===
    @Field("last_admin_action")
    private LocalDateTime lastAdminAction;

    @Field("total_actions_performed")
    private Integer totalActionsPerformed;

    @Field("users_managed")
    private Integer usersManaged;

    @Field("doctors_approved")
    private Integer doctorsApproved;

    @Field("clinics_approved")
    private Integer clinicsApproved;

    // === BUSINESS METHODS ===

    public boolean isSuperAdmin() {
        return AdminLevel.SUPER_ADMIN.equals(adminLevel);
    }

    public boolean canPerformAction(AdminPermission permission) {
        return permissions != null && permissions.contains(permission);
    }

    public boolean canManageUserType(String userType) {
        switch (userType.toUpperCase()) {
            case "PATIENT":
                return canManageUsers;
            case "DOCTOR":
                return canManageDoctors;
            case "CLINIC":
                return canManageClinics;
            case "ADMIN":
                return isSuperAdmin();
            default:
                return false;
        }
    }

    public String getAdminDisplayName() {
        return String.format("%s - %s (%s)", 
            getFullName(), 
            jobTitle != null ? jobTitle : "Admin", 
            department != null ? department : "Genel"
        );
    }

    public void recordAdminAction() {
        this.lastAdminAction = LocalDateTime.now();
        this.totalActionsPerformed = getTotalActionsPerformed() + 1;
    }

    // === GETTER METHODS WITH DEFAULTS ===
    
    public AdminLevel getAdminLevel() {
        return adminLevel != null ? adminLevel : AdminLevel.STANDARD;
    }

    public Boolean getCanManageUsers() {
        return canManageUsers != null ? canManageUsers : true;
    }

    public Boolean getCanManageDoctors() {
        return canManageDoctors != null ? canManageDoctors : true;
    }

    public Boolean getCanManageClinics() {
        return canManageClinics != null ? canManageClinics : false;
    }

    public Boolean getCanViewReports() {
        return canViewReports != null ? canViewReports : true;
    }

    public Boolean getCanManageSystem() {
        return canManageSystem != null ? canManageSystem : false;
    }

    public Integer getTotalActionsPerformed() {
        return totalActionsPerformed != null ? totalActionsPerformed : 0;
    }

    public Integer getUsersManaged() {
        return usersManaged != null ? usersManaged : 0;
    }

    public Integer getDoctorsApproved() {
        return doctorsApproved != null ? doctorsApproved : 0;
    }

    public Integer getClinicsApproved() {
        return clinicsApproved != null ? clinicsApproved : 0;
    }

    // === NESTED ENUMS ===

    public enum AdminLevel {
        STANDARD("Standart Admin"),
        SENIOR("Kıdemli Admin"),
        MANAGER("Yönetici"),
        SUPER_ADMIN("Süper Admin");

        private final String displayName;

        AdminLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum AdminPermission {
        // Kullanıcı yönetimi
        CREATE_USER("Kullanıcı Oluştur"),
        EDIT_USER("Kullanıcı Düzenle"),
        DELETE_USER("Kullanıcı Sil"),
        VIEW_USER_DETAILS("Kullanıcı Detayları Görüntüle"),

        // Doktor yönetimi
        APPROVE_DOCTOR("Doktor Onayla"),
        REJECT_DOCTOR("Doktor Reddet"),
        EDIT_DOCTOR("Doktor Düzenle"),
        VIEW_DOCTOR_REPORTS("Doktor Raporları Görüntüle"),

        // Klinik yönetimi
        APPROVE_CLINIC("Klinik Onayla"),
        REJECT_CLINIC("Klinik Reddet"),
        EDIT_CLINIC("Klinik Düzenle"),

        // Raporlar
        VIEW_FINANCIAL_REPORTS("Mali Raporlar"),
        VIEW_USER_STATISTICS("Kullanıcı İstatistikleri"),
        VIEW_APPOINTMENT_REPORTS("Randevu Raporları"),
        EXPORT_DATA("Veri Dışa Aktar"),

        // Sistem yönetimi
        MANAGE_SYSTEM_SETTINGS("Sistem Ayarları"),
        MANAGE_NOTIFICATIONS("Bildirim Yönetimi"),
        MANAGE_INTEGRATIONS("Entegrasyon Yönetimi"),
        ACCESS_AUDIT_LOGS("Audit Log Erişimi"),

        // Emergency
        EMERGENCY_ACCESS("Acil Durum Erişimi"),
        SYSTEM_MAINTENANCE("Sistem Bakımı");

        private final String displayName;

        AdminPermission(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}