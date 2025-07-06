// controller/TestController.java
package com.healthvia.platform.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthvia.platform.common.dto.ApiResponse;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.success(
            Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "message", "Health Via Platform is running!"
            ),
            "Sistem çalışıyor"
        );
    }
    
    @GetMapping("/protected")
    public ApiResponse<String> protectedEndpoint() {
        return ApiResponse.success("Bu korumalı bir endpoint!", "Başarılı!");
    }
}