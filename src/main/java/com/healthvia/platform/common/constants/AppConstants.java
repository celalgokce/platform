// common/constants/AppConstants.java
package com.healthvia.platform.common.constants;

public class AppConstants {
    
    // JWT
    public static final String JWT_TOKEN_HEADER = "Authorization";
    public static final String JWT_TOKEN_PREFIX = "Bearer ";
    public static final String JWT_SECRET_KEY = "jwt.secret";
    
    // Pagination
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
    
    // File Upload
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "webp"};
    public static final String[] ALLOWED_DOCUMENT_EXTENSIONS = {"pdf", "doc", "docx", "xls", "xlsx"};
    
    // Cache
    public static final String CACHE_CLINICS = "clinics";
    public static final String CACHE_DOCTORS = "doctors";
    public static final String CACHE_TREATMENTS = "treatments";
    
    // Regex
    public static final String PHONE_REGEX = "^[+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$";
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$";
    
    // Rate Limiting
    public static final String RATE_LIMIT_LOGIN = "login";
    public static final String RATE_LIMIT_REGISTER = "register";
    public static final String RATE_LIMIT_PASSWORD_RESET = "password-reset";
    
    // WebSocket
    public static final String WS_DESTINATION_PREFIX = "/app";
    public static final String WS_BROKER_PREFIX = "/topic";
    public static final String WS_USER_PREFIX = "/user";
    
    private AppConstants() {
        throw new IllegalStateException("Constants class");
    }
}