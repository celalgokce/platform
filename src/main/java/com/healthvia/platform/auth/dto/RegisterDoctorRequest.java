// auth/dto/RegisterDoctorRequest.java
package com.healthvia.platform.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDoctorRequest extends RegisterRequest {
    
    @NotBlank(message = "Diploma numarası boş olamaz")
    private String diplomaNumber;
    
    @NotBlank(message = "Tabip Odası sicil numarası boş olamaz")
    private String medicalLicenseNumber;
    
    @NotBlank(message = "Mezun olduğu tıp fakültesi boş olamaz")
    private String medicalSchool;
    
    private Integer graduationYear;
    
    @NotBlank(message = "Uzmanlık alanı boş olamaz")
    private String primarySpecialty;
    
    private String specialtySchool;
    private Integer specialtyCompletionYear;
    private Integer yearsOfExperience;
    private String currentHospital;
    private String currentClinic;
}