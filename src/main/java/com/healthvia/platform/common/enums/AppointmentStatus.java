// common/enums/AppointmentStatus.java
package com.healthvia.platform.common.enums;

import lombok.Getter;

@Getter
public enum AppointmentStatus {
    PENDING("Onay Bekliyor"),
    CONFIRMED("Onaylandı"),
    IN_PROGRESS("Devam Ediyor"),
    COMPLETED("Tamamlandı"),
    CANCELLED("İptal Edildi"),
    NO_SHOW("Gelmedi");
    
    private final String displayName;
    
    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public boolean isCancellable() {
        return this == PENDING || this == CONFIRMED;
    }
    
    public boolean isActive() {
        return this == PENDING || this == CONFIRMED || this == IN_PROGRESS;
    }
}