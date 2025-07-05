// common/enums/UserStatus.java
package com.healthvia.platform.common.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    PENDING_VERIFICATION("Doğrulama Bekliyor"),
    ACTIVE("Aktif"),
    INACTIVE("Pasif"),
    SUSPENDED("Askıya Alınmış"),
    DELETED("Silinmiş");
    
    private final String displayName;
    
    UserStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public boolean isActive() {
        return this == ACTIVE;
    }
}