// config/MongoConfig.java
package com.healthvia.platform.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.healthvia.platform.common.util.SecurityUtils;

@Configuration
@EnableMongoRepositories(basePackages = "com.healthvia.platform")
@EnableMongoAuditing
public class MongoConfig {
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            String userId = SecurityUtils.getCurrentUserIdOrNull();
            return Optional.ofNullable(userId != null ? userId : "system");
        };
    }
    
    @Bean
    public MappingMongoConverter mappingMongoConverter(
            MongoMappingContext mongoMappingContext,
            DbRefResolver dbRefResolver) {
        
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        // _class field'ını kaldır
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }
}