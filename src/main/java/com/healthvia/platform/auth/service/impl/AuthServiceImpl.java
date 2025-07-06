// auth/service/impl/AuthServiceImpl.java
package com.healthvia.platform.auth.service.impl;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.healthvia.platform.admin.entity.Admin;
import com.healthvia.platform.auth.dto.AuthResponse;
import com.healthvia.platform.auth.dto.LoginRequest;
import com.healthvia.platform.auth.dto.RegisterAdminRequest;
import com.healthvia.platform.auth.dto.RegisterDoctorRequest;
import com.healthvia.platform.auth.dto.RegisterRequest;
import com.healthvia.platform.auth.security.JwtTokenProvider;
import com.healthvia.platform.auth.security.UserPrincipal;
import com.healthvia.platform.auth.service.AuthService;
import com.healthvia.platform.common.constants.ErrorCodes;
import com.healthvia.platform.common.enums.UserRole;
import com.healthvia.platform.common.enums.UserStatus;
import com.healthvia.platform.common.exception.BusinessException;
import com.healthvia.platform.doctor.entity.Doctor;
import com.healthvia.platform.user.entity.Patient;
import com.healthvia.platform.user.entity.User;
import com.healthvia.platform.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    // === ROLE-SPECIFIC REGISTRATIONS ===
    
    @Override
    public AuthResponse registerPatient(RegisterRequest request) {
        log.debug("Registering patient with email: {}", request.getEmail());
        
        validateCommonFields(request);
        
        // Create Patient entity
        Patient patient = Patient.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .password(request.getPassword())
            .role(UserRole.PATIENT)
            .status(UserStatus.PENDING_VERIFICATION)
            .gender(request.getGender())
            .birthDate(request.getBirthDate())
            .province(request.getProvince())
            .district(request.getDistrict())
            .emailVerified(false)
            .phoneVerified(false)
            .gdprConsent(request.getGdprConsent())
            .gdprConsentDate(LocalDateTime.now())
            .tcKimlikNo(request.getTcKimlikNo())
            .passportNo(request.getPassportNo())
            .birthPlace(request.getBirthPlace())
            .build();
        
        User savedUser = userService.createUser(patient);
        
        AuthResponse response = createAuthResponse(savedUser);
        response.setMessage("Email doğrulaması gerekli");
        response.setRequiresAction(true);
        
        return response;
    }

    @Override
    public AuthResponse registerDoctor(RegisterDoctorRequest request) {
        log.debug("Registering doctor with email: {}", request.getEmail());
        
        validateCommonFields(request);
        validateDoctorFields(request);
        
        // Create Doctor entity
        Doctor doctor = Doctor.builder()
            // User fields
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .password(request.getPassword())
            .role(UserRole.DOCTOR)
            .status(UserStatus.PENDING_VERIFICATION)
            .gender(request.getGender())
            .birthDate(request.getBirthDate())
            .province(request.getProvince())
            .district(request.getDistrict())
            .emailVerified(false)
            .phoneVerified(false)
            .gdprConsent(request.getGdprConsent())
            .gdprConsentDate(LocalDateTime.now())
            
            // Doctor specific fields
            .diplomaNumber(request.getDiplomaNumber())
            .medicalLicenseNumber(request.getMedicalLicenseNumber())
            .medicalSchool(request.getMedicalSchool())
            .graduationYear(request.getGraduationYear())
            .primarySpecialty(request.getPrimarySpecialty())
            .specialtySchool(request.getSpecialtySchool())
            .specialtyCompletionYear(request.getSpecialtyCompletionYear())
            .yearsOfExperience(request.getYearsOfExperience())
            .currentHospital(request.getCurrentHospital())
            .currentClinic(request.getCurrentClinic())
            .verificationStatus(Doctor.VerificationStatus.PENDING)
            .isAcceptingNewPatients(false) // Başlangıçta kapalı
            .build();
        
        User savedUser = userService.createUser(doctor);
        
        AuthResponse response = createAuthResponse(savedUser);
        response.setMessage("Hesabınız inceleme altındadır");
        response.setRequiresAction(true);
        
        return response;
    }

    @Override
    public AuthResponse registerAdmin(RegisterAdminRequest request) {
        log.debug("Registering admin with email: {}", request.getEmail());
        
        validateCommonFields(request);
        validateAdminFields(request);
        
        // Create Admin entity
        Admin admin = Admin.builder()
            // User fields
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .password(request.getPassword())
            .role(UserRole.ADMIN)
            .status(UserStatus.ACTIVE) // Admin'ler direkt aktif
            .gender(request.getGender())
            .birthDate(request.getBirthDate())
            .province(request.getProvince())
            .district(request.getDistrict())
            .emailVerified(true) // Admin'ler için email doğrulaması atlanabilir
            .phoneVerified(false)
            .gdprConsent(request.getGdprConsent())
            .gdprConsentDate(LocalDateTime.now())
            
            // Admin specific fields
            .department(request.getDepartment())
            .jobTitle(request.getJobTitle())
            .employeeId(request.getEmployeeId())
            .adminLevel(request.getAdminLevel() != null ? request.getAdminLevel() : Admin.AdminLevel.STANDARD)
            .permissions(request.getPermissions())
            .supervisorId(request.getSupervisorId())
            .hireDate(request.getHireDate() != null ? request.getHireDate() : LocalDateTime.now())
            .canManageUsers(request.getCanManageUsers() != null ? request.getCanManageUsers() : true)
            .canManageDoctors(request.getCanManageDoctors() != null ? request.getCanManageDoctors() : false)
            .canManageClinics(request.getCanManageClinics() != null ? request.getCanManageClinics() : false)
            .canViewReports(request.getCanViewReports() != null ? request.getCanViewReports() : true)
            .canManageSystem(request.getCanManageSystem() != null ? request.getCanManageSystem() : false)
            .build();
        
        User savedUser = userService.createUser(admin);
        
        AuthResponse response = createAuthResponse(savedUser);
        response.setMessage("Admin hesabı oluşturuldu");
        response.setRequiresAction(false);
        
        return response;
    }

    // === AUTHENTICATION ===
    
    @Override
    public AuthResponse login(LoginRequest request) {
        log.debug("Login attempt for username: {}", request.getUsername());
        
        // User bilgilerini al (authentication'dan önce kontroller için)
        User user = userService.findByEmailOrPhone(request.getUsername())
            .orElseThrow(() -> new BusinessException(ErrorCodes.INVALID_CREDENTIALS));
        
        // Hesap kontrolü
        if (user.isAccountLocked()) {
            throw new BusinessException(ErrorCodes.ACCOUNT_LOCKED);
        }
        
        if (!user.getEmailVerified() && user.getRole() != UserRole.ADMIN) {
            throw new BusinessException(ErrorCodes.EMAIL_NOT_VERIFIED);
        }
        
        try {
            // Authentication
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
        } catch (Exception e) {
            // Başarısız giriş denemesi
            user.incrementFailedLoginAttempts();
            userService.updateUser(user.getId(), user);
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS);
        }
        
        // Son giriş tarihini güncelle
        user.updateLastLogin();
        userService.updateUser(user.getId(), user);
        
        AuthResponse response = createAuthResponse(user);
        response.setMessage("Giriş başarılı");
        response.setRequiresAction(false);
        
        return response;
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        // Token geçerliliğini kontrol et
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCodes.TOKEN_INVALID);
        }
        
        // User ID'yi al
        String userId = tokenProvider.getUserIdFromToken(refreshToken);
        
        // User'ı bul
        User user = userService.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCodes.USER_NOT_FOUND));
        
        return createAuthResponse(user);
    }

    // === EMAIL & PASSWORD ===
    
    @Override
    public void verifyEmail(String token) {
        // Email doğrulama implementasyonu
        // Bu kısım email service ile entegre edilecek
        throw new UnsupportedOperationException("Email verification will be implemented with email service");
    }

    @Override
    public void forgotPassword(String email) {
        // Kullanıcı var mı kontrol et
        User user = userService.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCodes.USER_NOT_FOUND));
        
        // Şifre sıfırlama token'ı oluştur ve email gönder
        // Bu kısım email service ile entegre edilecek
        log.info("Password reset requested for user: {}", user.getId());
        throw new UnsupportedOperationException("Password reset will be implemented with email service");
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // Token doğrula ve şifreyi güncelle
        // Bu kısım email service ile entegre edilecek
        throw new UnsupportedOperationException("Password reset will be implemented with email service");
    }

    // === PRIVATE HELPER METHODS ===

    private void validateCommonFields(RegisterRequest request) {
        // Email benzersizlik kontrolü
        if (!userService.isEmailAvailable(request.getEmail())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Email already exists: " + request.getEmail());
        }
        
        // Telefon benzersizlik kontrolü
        if (!userService.isPhoneAvailable(request.getPhone())) {
            throw new BusinessException(ErrorCodes.USER_ALREADY_EXISTS, "Phone already exists: " + request.getPhone());
        }
        
        // GDPR consent kontrolü
        if (!request.getGdprConsent()) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "GDPR consent is required");
        }
    }

    private void validateDoctorFields(RegisterDoctorRequest request) {
        if (request.getDiplomaNumber() == null || request.getDiplomaNumber().trim().isEmpty()) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Diploma number is required");
        }
        
        if (request.getMedicalLicenseNumber() == null || request.getMedicalLicenseNumber().trim().isEmpty()) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Medical license number is required");
        }
        
        if (request.getPrimarySpecialty() == null || request.getPrimarySpecialty().trim().isEmpty()) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Primary specialty is required");
        }
        
        // TODO: Doctor service'ten diploma ve license benzersizlik kontrolü
    }

    private void validateAdminFields(RegisterAdminRequest request) {
        if (request.getDepartment() == null || request.getDepartment().trim().isEmpty()) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Department is required");
        }
        
        if (request.getEmployeeId() == null || request.getEmployeeId().trim().isEmpty()) {
            throw new BusinessException(ErrorCodes.INVALID_CREDENTIALS, "Employee ID is required");
        }
        
        // TODO: Admin service'ten employee ID benzersizlik kontrolü
    }

    private AuthResponse createAuthResponse(User user) {
        // UserPrincipal oluştur
        UserPrincipal userPrincipal = createUserPrincipal(user);
        
        // Token'ları oluştur
        String accessToken = tokenProvider.generateAccessToken(userPrincipal);
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());
        
        log.info("Authentication successful for user: {}", user.getId());
        
        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(900000) // 15 dakika
            .userId(user.getId())
            .email(user.getEmail())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole())
            .status(user.getStatus())
            .emailVerified(user.getEmailVerified())
            .phoneVerified(user.getPhoneVerified())
            .lastLoginDate(user.getLastLoginDate())
            .build();
    }

    private UserPrincipal createUserPrincipal(User user) {
        return UserPrincipal.builder()
            .id(user.getId())
            .email(user.getEmail())
            .password(user.getPassword())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .role(user.getRole())
            .status(user.getStatus())
            .emailVerified(user.getEmailVerified())
            .build();
    }
}