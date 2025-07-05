// common/enums/UserRole.java
package com.healthvia.platform.common.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("Admin", "Sistem Yöneticisi"),
    DOCTOR("Doctor", "Doktor"),
    PATIENT("Patient", "Hasta");
    
    private final String code;
    private final String displayName;
    
    UserRole(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    public boolean isDoctor() {
        return this == DOCTOR;
    }
    
    public boolean isPatient() {
        return this == PATIENT;
    }
}