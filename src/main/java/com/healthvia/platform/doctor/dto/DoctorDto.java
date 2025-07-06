// doctor/dto/DoctorDto.java
package com.healthvia.platform.doctor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import com.healthvia.platform.common.enums.Language;
import com.healthvia.platform.common.enums.UserRole;
import com.healthvia.platform.common.enums.UserStatus;
import com.healthvia.platform.doctor.entity.Doctor;
import com.healthvia.platform.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {
    
    // === USER FIELDS ===
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
    
    // === PROFESSIONAL IDENTITY ===
    private String diplomaNumber;
    private String medicalLicenseNumber;
    
    // === EDUCATION ===
    private String medicalSchool;
    private Integer graduationYear;
    private String specialtySchool;
    private Integer specialtyCompletionYear;
    
    // === SPECIALTIES ===
    private String primarySpecialty;
    private Set<String> subspecialties;
    private Set<String> medicalInterests;
    
    // === EXPERIENCE ===
    private Integer yearsOfExperience;
    private String currentHospital;
    private String currentClinic;
    private String jobTitle;
    
    // === CONSULTATION INFO ===
    private BigDecimal consultationFee;
    private Integer consultationDurationMinutes;
    private Set<String> workingDays;
    private LocalTime workingHoursStart;
    private LocalTime workingHoursEnd;
    private LocalTime lunchBreakStart;
    private LocalTime lunchBreakEnd;
    private Integer appointmentBufferMinutes;
    
    // === PROFILE ===
    private String biography;
    private String curriculum;
    private List<String> awardsAndRecognitions;
    private List<String> publications;
    private List<String> conferencesAttended;
    
    // === CERTIFICATIONS ===
    private List<CertificationDto> certifications;
    private Set<String> professionalMemberships;
    
    // === SERVICES ===
    private Set<String> treatableConditions;
    private Set<String> offeredServices;
    private Set<String> languagesSpoken;
    
    // === STATISTICS ===
    private Integer totalAppointments;
    private Integer completedAppointments;
    private Integer cancelledAppointments;
    private Double averageRating;
    private Integer totalReviews;
    private Integer patientsTreated;
    
    // === STATUS ===
    private Boolean isAcceptingNewPatients;
    private Boolean isAvailableForEmergencies;
    private Set<Doctor.ConsultationType> consultationTypes;
    private Doctor.VerificationStatus verificationStatus;
    private LocalDate verificationDate;
    
    // === COMPUTED METHODS ===
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public int getAge() {
        if (birthDate == null) return 0;
        return LocalDate.now().getYear() - birthDate.getYear();
    }
    
    public boolean isVerified() {
        return Doctor.VerificationStatus.VERIFIED.equals(verificationStatus);
    }
    
    public boolean isExperienced() {
        return yearsOfExperience != null && yearsOfExperience >= 5;
    }
    
    public boolean hasHighRating() {
        return averageRating != null && averageRating >= 4.0;
    }
    
    public double getAppointmentCompletionRate() {
        if (totalAppointments == null || totalAppointments == 0) return 0.0;
        return (completedAppointments != null ? completedAppointments : 0) * 100.0 / totalAppointments;
    }
    
    public String getSpecialtyDisplay() {
        StringBuilder sb = new StringBuilder();
        if (primarySpecialty != null) {
            sb.append(primarySpecialty);
        }
        if (subspecialties != null && !subspecialties.isEmpty()) {
            sb.append(" (").append(String.join(", ", subspecialties)).append(")");
        }
        return sb.toString();
    }
    
    public String getExperienceDisplay() {
        if (yearsOfExperience == null) return "Belirtilmemiş";
        return yearsOfExperience + " yıl deneyim";
    }
    
    public String getWorkingHoursDisplay() {
        if (workingHoursStart == null || workingHoursEnd == null) return "Belirtilmemiş";
        return workingHoursStart + " - " + workingHoursEnd;
    }
    
    // === FACTORY METHODS ===
    
    public static DoctorDto fromEntity(Doctor doctor) {
        if (doctor == null) return null;
        
        return DoctorDto.builder()
            // User fields
            .id(doctor.getId())
            .firstName(doctor.getFirstName())
            .lastName(doctor.getLastName())
            .email(doctor.getEmail())
            .phone(doctor.getPhone())
            .gender(doctor.getGender())
            .birthDate(doctor.getBirthDate())
            .province(doctor.getProvince())
            .district(doctor.getDistrict())
            .role(doctor.getRole())
            .status(doctor.getStatus())
            .emailVerified(doctor.getEmailVerified())
            .phoneVerified(doctor.getPhoneVerified())
            .profileCompletionRate(doctor.getProfileCompletionRate())
            .lastLoginDate(doctor.getLastLoginDate())
            .preferredLanguage(doctor.getPreferredLanguage())
            .avatarUrl(doctor.getAvatarUrl())
            .createdAt(doctor.getCreatedAt())
            .updatedAt(doctor.getUpdatedAt())
            
            // Doctor specific fields
            .diplomaNumber(doctor.getDiplomaNumber())
            .medicalLicenseNumber(doctor.getMedicalLicenseNumber())
            .medicalSchool(doctor.getMedicalSchool())
            .graduationYear(doctor.getGraduationYear())
            .specialtySchool(doctor.getSpecialtySchool())
            .specialtyCompletionYear(doctor.getSpecialtyCompletionYear())
            .primarySpecialty(doctor.getPrimarySpecialty())
            .subspecialties(doctor.getSubspecialties())
            .medicalInterests(doctor.getMedicalInterests())
            .yearsOfExperience(doctor.getYearsOfExperience())
            .currentHospital(doctor.getCurrentHospital())
            .currentClinic(doctor.getCurrentClinic())
            .jobTitle(doctor.getJobTitle())
            .consultationFee(doctor.getConsultationFee())
            .consultationDurationMinutes(doctor.getConsultationDurationMinutes())
            .workingDays(doctor.getWorkingDays())
            .workingHoursStart(doctor.getWorkingHoursStart())
            .workingHoursEnd(doctor.getWorkingHoursEnd())
            .lunchBreakStart(doctor.getLunchBreakStart())
            .lunchBreakEnd(doctor.getLunchBreakEnd())
            .appointmentBufferMinutes(doctor.getAppointmentBufferMinutes())
            .biography(doctor.getBiography())
            .curriculum(doctor.getCurriculum())
            .awardsAndRecognitions(doctor.getAwardsAndRecognitions())
            .publications(doctor.getPublications())
            .conferencesAttended(doctor.getConferencesAttended())
            .certifications(doctor.getCertifications() != null ? 
                doctor.getCertifications().stream()
                    .map(CertificationDto::fromEntity)
                    .toList() : null)
            .professionalMemberships(doctor.getProfessionalMemberships())
            .treatableConditions(doctor.getTreatableConditions())
            .offeredServices(doctor.getOfferedServices())
            .languagesSpoken(doctor.getLanguagesSpoken())
            .totalAppointments(doctor.getTotalAppointments())
            .completedAppointments(doctor.getCompletedAppointments())
            .cancelledAppointments(doctor.getCancelledAppointments())
            .averageRating(doctor.getAverageRating())
            .totalReviews(doctor.getTotalReviews())
            .patientsTreated(doctor.getPatientsTreated())
            .isAcceptingNewPatients(doctor.getIsAcceptingNewPatients())
            .isAvailableForEmergencies(doctor.getIsAvailableForEmergencies())
            .consultationTypes(doctor.getConsultationTypes())
            .verificationStatus(doctor.getVerificationStatus())
            .verificationDate(doctor.getVerificationDate())
            .build();
    }
    
    public static DoctorDto fromEntityBasic(Doctor doctor) {
        if (doctor == null) return null;
        
        return DoctorDto.builder()
            .id(doctor.getId())
            .firstName(doctor.getFirstName())
            .lastName(doctor.getLastName())
            .primarySpecialty(doctor.getPrimarySpecialty())
            .currentHospital(doctor.getCurrentHospital())
            .yearsOfExperience(doctor.getYearsOfExperience())
            .averageRating(doctor.getAverageRating())
            .consultationFee(doctor.getConsultationFee())
            .isAcceptingNewPatients(doctor.getIsAcceptingNewPatients())
            .verificationStatus(doctor.getVerificationStatus())
            .province(doctor.getProvince())
            .district(doctor.getDistrict())
            .avatarUrl(doctor.getAvatarUrl())
            .build();
    }
    
    public Doctor toEntity() {
        Doctor doctor = Doctor.builder()
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
            
            // Doctor specific fields
            .diplomaNumber(this.diplomaNumber)
            .medicalLicenseNumber(this.medicalLicenseNumber)
            .medicalSchool(this.medicalSchool)
            .graduationYear(this.graduationYear)
            .specialtySchool(this.specialtySchool)
            .specialtyCompletionYear(this.specialtyCompletionYear)
            .primarySpecialty(this.primarySpecialty)
            .subspecialties(this.subspecialties)
            .medicalInterests(this.medicalInterests)
            .yearsOfExperience(this.yearsOfExperience)
            .currentHospital(this.currentHospital)
            .currentClinic(this.currentClinic)
            .jobTitle(this.jobTitle)
            .consultationFee(this.consultationFee)
            .consultationDurationMinutes(this.consultationDurationMinutes)
            .workingDays(this.workingDays)
            .workingHoursStart(this.workingHoursStart)
            .workingHoursEnd(this.workingHoursEnd)
            .lunchBreakStart(this.lunchBreakStart)
            .lunchBreakEnd(this.lunchBreakEnd)
            .appointmentBufferMinutes(this.appointmentBufferMinutes)
            .biography(this.biography)
            .curriculum(this.curriculum)
            .awardsAndRecognitions(this.awardsAndRecognitions)
            .publications(this.publications)
            .conferencesAttended(this.conferencesAttended)
            .certifications(this.certifications != null ?
                this.certifications.stream()
                    .map(CertificationDto::toEntity)
                    .toList() : null)
            .professionalMemberships(this.professionalMemberships)
            .treatableConditions(this.treatableConditions)
            .offeredServices(this.offeredServices)
            .languagesSpoken(this.languagesSpoken)
            .totalAppointments(this.totalAppointments)
            .completedAppointments(this.completedAppointments)
            .cancelledAppointments(this.cancelledAppointments)
            .averageRating(this.averageRating)
            .totalReviews(this.totalReviews)
            .patientsTreated(this.patientsTreated)
            .isAcceptingNewPatients(this.isAcceptingNewPatients)
            .isAvailableForEmergencies(this.isAvailableForEmergencies)
            .consultationTypes(this.consultationTypes)
            .verificationStatus(this.verificationStatus)
            .verificationDate(this.verificationDate)
            .build();
            
        return doctor;
    }
    
    // === NESTED DTO ===
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CertificationDto {
        private String name;
        private String issuer;
        private LocalDate issueDate;
        private LocalDate expiryDate;
        private String certificateNumber;
        private boolean isValid;
        
        public boolean isExpired() {
            return expiryDate != null && expiryDate.isBefore(LocalDate.now());
        }
        
        public static CertificationDto fromEntity(Doctor.Certification certification) {
            if (certification == null) return null;
            
            return CertificationDto.builder()
                .name(certification.getName())
                .issuer(certification.getIssuer())
                .issueDate(certification.getIssueDate())
                .expiryDate(certification.getExpiryDate())
                .certificateNumber(certification.getCertificateNumber())
                .isValid(certification.isValid())
                .build();
        }
        
        public Doctor.Certification toEntity() {
            return new Doctor.Certification(
                this.name,
                this.issuer,
                this.issueDate,
                this.expiryDate,
                this.certificateNumber,
                this.isValid
            );
        }
    }
}