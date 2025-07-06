// config/MongoConfig.java
package com.healthvia.platform.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.healthvia.platform.common.util.SecurityUtils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableMongoRepositories(basePackages = "com.healthvia.platform")
@EnableMongoAuditing
@RequiredArgsConstructor
public class MongoConfig {
    
    private final MappingMongoConverter mappingMongoConverter;
    
    @PostConstruct
    public void setUpMongoEscapeCharacterConversion() {
        // _class field'ını kaldır (Spring Boot 3.x uyumlu)
        mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
    }
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            String userId = SecurityUtils.getCurrentUserIdOrNull();
            return Optional.ofNullable(userId != null ? userId : "system");
        };
    }
}