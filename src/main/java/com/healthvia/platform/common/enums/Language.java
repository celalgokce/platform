// common/enums/Language.java
package com.healthvia.platform.common.enums;

import lombok.Getter;

@Getter
public enum Language {
    TURKISH("tr", "Türkçe", "🇹🇷"),
    ENGLISH("en", "English", "🇺🇸"),
    ARABIC("ar", "العربية", "🇸🇦"),
    RUSSIAN("ru", "Русский", "🇷🇺"),
    GERMAN("de", "Deutsch", "🇩🇪"),
    FRENCH("fr", "Français", "🇫🇷");

    private final String code;
    private final String displayName;
    private final String flag;

    Language(String code, String displayName, String flag) {
        this.code = code;
        this.displayName = displayName;
        this.flag = flag;
    }

    public static Language fromCode(String code) {
        for (Language lang : values()) {
            if (lang.code.equals(code)) {
                return lang;
            }
        }
        return TURKISH; // Default
    }

    public boolean isRightToLeft() {
        return this == ARABIC;
    }

    public String getDisplayWithFlag() {
        return flag + " " + displayName;
    }
}