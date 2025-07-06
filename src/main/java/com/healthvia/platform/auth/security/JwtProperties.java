// auth/security/JwtProperties.java
package com.healthvia.platform.auth.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private long accessTokenExpiration = 900000; // 15 dakika
    private long refreshTokenExpiration = 604800000; // 7 gün
}