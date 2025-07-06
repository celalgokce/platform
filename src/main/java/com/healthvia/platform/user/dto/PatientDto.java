// user/dto/PatientDto.java - @SuperBuilder olmadan
package com.healthvia.platform.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.healthvia.platform.common.enums.Language;
import com.healthvia.platform.common.enums.UserRole;
import com.healthvia.platform.common.enums.UserStatus;
import com.healthvia.platform.user.entity.Patient;
import com.healthvia.platform.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {
    
    // === USER FIELDS (Copy from UserDto) ===
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private User.Gender gender;
    private LocalDate birthDate;
    private String province;
    private String district;
    private UserRole role;
    private UserStatus status;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Integer profileCompletionRate;
    private LocalDateTime lastLoginDate;
    private Language preferredLanguage;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // === PATIENT SPECIFIC FIELDS ===
    private String tcKimlikNo;
    private String passportNo;
    private String birthPlace;
    private String address;
    private String postalCode;
    
    // === HEALTH INFORMATION ===
    private String bloodType;
    private Integer heightCm;
    private Double weightKg;
    private String allergies;
    private String chronicDiseases;
    private String currentMedications;
    private String surgeryHistory;
    private String familyMedicalHistory;
    
    // === EMERGENCY CONTACT ===
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;
    
    // === INSURANCE INFORMATION ===
    private boolean hasInsurance;
    private String insuranceCompany;
    private String insurancePolicyNumber;
    private LocalDate insuranceExpiryDate;
    
    // === PREFERENCES ===
    private User.Gender preferredDoctorGender;
    private Patient.SmokingStatus smokingStatus;
    private Patient.AlcoholConsumption alcoholConsumption;
    private Patient.ExerciseFrequency exerciseFrequency;
    
    // === STATISTICS ===
    private Integer totalAppointments;
    private Integer completedAppointments;
    private Integer cancelledAppointments;
    private LocalDate lastAppointmentDate;
    
    // === COMPUTED METHODS ===
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public int getAge() {
        if (birthDate == null) return 0;
        return LocalDate.now().getYear() - birthDate.getYear();
    }
    
    public Double getBMI() {
        if (heightCm == null || weightKg == null || heightCm == 0) {
            return null;
        }
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }
    
    public String getBMICategory() {
        Double bmi = getBMI();
        if (bmi == null) return "Bilinmiyor";
        
        if (bmi < 18.5) return "Zayıf";
        if (bmi < 25) return "Normal";
        if (bmi < 30) return "Fazla Kilolu";
        return "Obez";
    }
    
    public boolean hasValidIdentity() {
        return (tcKimlikNo != null && !tcKimlikNo.trim().isEmpty()) || 
               (passportNo != null && !passportNo.trim().isEmpty());
    }
    
    public boolean hasHealthIssues() {
        return (allergies != null && !allergies.trim().isEmpty()) ||
               (chronicDiseases != null && !chronicDiseases.trim().isEmpty()) ||
               (currentMedications != null && !currentMedications.trim().isEmpty());
    }
    
    public double getAppointmentCompletionRate() {
        if (totalAppointments == null || totalAppointments == 0) return 0.0;
        return (completedAppointments != null ? completedAppointments : 0) * 100.0 / totalAppointments;
    }
    
    // === FACTORY METHODS ===
    
    public static PatientDto fromEntity(Patient patient) {
        if (patient == null) return null;
        
        return PatientDto.builder()
            // User fields
            .id(patient.getId())
            .firstName(patient.getFirstName())
            .lastName(patient.getLastName())
            .email(patient.getEmail())
            .phone(patient.getPhone())
            .gender(patient.getGender())
            .birthDate(patient.getBirthDate())
            .province(patient.getProvince())
            .district(patient.getDistrict())
            .role(patient.getRole())
            .status(patient.getStatus())
            .emailVerified(patient.getEmailVerified())
            .phoneVerified(patient.getPhoneVerified())
            .profileCompletionRate(patient.getProfileCompletionRate())
            .lastLoginDate(patient.getLastLoginDate())
            .preferredLanguage(patient.getPreferredLanguage())
            .avatarUrl(patient.getAvatarUrl())
            .createdAt(patient.getCreatedAt())
            .updatedAt(patient.getUpdatedAt())
            
            // Patient specific fields
            .tcKimlikNo(patient.getTcKimlikNo())
            .passportNo(patient.getPassportNo())
            .birthPlace(patient.getBirthPlace())
            .address(patient.getAddress())
            .postalCode(patient.getPostalCode())
            .bloodType(patient.getBloodType())
            .heightCm(patient.getHeightCm())
            .weightKg(patient.getWeightKg())
            .allergies(patient.getAllergies())
            .chronicDiseases(patient.getChronicDiseases())
            .currentMedications(patient.getCurrentMedications())
            .surgeryHistory(patient.getSurgeryHistory())
            .familyMedicalHistory(patient.getFamilyMedicalHistory())
            .emergencyContactName(patient.getEmergencyContactName())
            .emergencyContactPhone(patient.getEmergencyContactPhone())
            .emergencyContactRelationship(patient.getEmergencyContactRelationship())
            .hasInsurance(patient.isHasInsurance())
            .insuranceCompany(patient.getInsuranceCompany())
            .insurancePolicyNumber(patient.getInsurancePolicyNumber())
            .insuranceExpiryDate(patient.getInsuranceExpiryDate())
            .preferredDoctorGender(patient.getPreferredDoctorGender())
            .smokingStatus(patient.getSmokingStatus())
            .alcoholConsumption(patient.getAlcoholConsumption())
            .exerciseFrequency(patient.getExerciseFrequency())
            .totalAppointments(patient.getTotalAppointments())
            .completedAppointments(patient.getCompletedAppointments())
            .cancelledAppointments(patient.getCancelledAppointments())
            .lastAppointmentDate(patient.getLastAppointmentDate())
            .build();
    }
    
    public Patient toEntity() {
        Patient patient = Patient.builder()
            // User fields
            .id(this.id)
            .firstName(this.firstName)
            .lastName(this.lastName)
            .email(this.email)
            .phone(this.phone)
            .gender(this.gender)
            .birthDate(this.birthDate)
            .province(this.province)
            .district(this.district)
            .role(this.role)
            .status(this.status)
            .emailVerified(this.emailVerified)
            .phoneVerified(this.phoneVerified)
            .profileCompletionRate(this.profileCompletionRate)
            .lastLoginDate(this.lastLoginDate)
            .preferredLanguage(this.preferredLanguage)
            .avatarUrl(this.avatarUrl)
            
            // Patient specific fields
            .tcKimlikNo(this.tcKimlikNo)
            .passportNo(this.passportNo)
            .birthPlace(this.birthPlace)
            .address(this.address)
            .postalCode(this.postalCode)
            .bloodType(this.bloodType)
            .heightCm(this.heightCm)
            .weightKg(this.weightKg)
            .allergies(this.allergies)
            .chronicDiseases(this.chronicDiseases)
            .currentMedications(this.currentMedications)
            .surgeryHistory(this.surgeryHistory)
            .familyMedicalHistory(this.familyMedicalHistory)
            .emergencyContactName(this.emergencyContactName)
            .emergencyContactPhone(this.emergencyContactPhone)
            .emergencyContactRelationship(this.emergencyContactRelationship)
            .hasInsurance(this.hasInsurance)
            .insuranceCompany(this.insuranceCompany)
            .insurancePolicyNumber(this.insurancePolicyNumber)
            .insuranceExpiryDate(this.insuranceExpiryDate)
            .preferredDoctorGender(this.preferredDoctorGender)
            .smokingStatus(this.smokingStatus)
            .alcoholConsumption(this.alcoholConsumption)
            .exerciseFrequency(this.exerciseFrequency)
            .totalAppointments(this.totalAppointments)
            .completedAppointments(this.completedAppointments)
            .cancelledAppointments(this.cancelledAppointments)
            .lastAppointmentDate(this.lastAppointmentDate)
            .build();
            
        return patient;
    }
}