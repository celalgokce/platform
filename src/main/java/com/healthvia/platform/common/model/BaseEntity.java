// common/model/BaseEntity.java
package com.healthvia.platform.common.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    
    @Id
    private String id;
    
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;
    
    @CreatedBy
    @Field("created_by")
    private String createdBy;
    
    @LastModifiedBy
    @Field("updated_by")
    private String updatedBy;
    
    @Version
    private Long version;
    
    @Field("is_deleted")
    private boolean deleted = false;
    
    @Field("deleted_at")
    private LocalDateTime deletedAt;
    
    @Field("deleted_by")
    private String deletedBy;
    
    public void markAsDeleted(String deletedBy) {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
    
    public boolean isNew() {
        return id == null;
    }
    
    public boolean isDeleted() {
        return deleted;
    }
}