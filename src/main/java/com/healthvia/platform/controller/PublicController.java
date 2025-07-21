package com.healthvia.platform.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.healthvia.platform.common.dto.ApiResponse;

@RestController
@RequestMapping("/api/v1/public")
public class PublicController {
    
    @GetMapping("/clinics")
    public ApiResponse<List<Map<String, Object>>> getClinics() {
        
        List<Map<String, Object>> clinics = List.of(
            Map.of(
                "id", "1",
                "name", "Acıbadem Hastanesi",
                "city", "İstanbul",
                "district", "Kadıköy",
                "rating", 4.5
            ),
            Map.of(
                "id", "2", 
                "name", "Memorial Hastanesi",
                "city", "İstanbul",
                "district", "Şişli",
                "rating", 4.3
            )
        );
        
        return ApiResponse.success(clinics);
    }
    
    @GetMapping("/doctors")
    public ApiResponse<List<Map<String, Object>>> getDoctors() {
        
        List<Map<String, Object>> doctors = List.of(
            Map.of(
                "id", "1",
                "name", "Dr. Mehmet Kaya",
                "specialty", "Kardiyoloji",
                "hospital", "Acıbadem Hastanesi",
                "rating", 4.8
            ),
            Map.of(
                "id", "2",
                "name", "Dr. Ayşe Demir",
                "specialty", "Nöroloji", 
                "hospital", "Memorial Hastanesi",
                "rating", 4.6
            )
        );
        
        return ApiResponse.success(doctors);
    }
    
    @GetMapping("/treatments")
    public ApiResponse<List<Map<String, Object>>> getTreatments() {
        
        List<Map<String, Object>> treatments = List.of(
            Map.of(
                "id", "1",
                "name", "Genel Muayene",
                "price", 150,
                "duration", 30
            ),
            Map.of(
                "id", "2",
                "name", "Kardiyoloji Konsültasyonu",
                "price", 300,
                "duration", 45
            )
        );
        
        return ApiResponse.success(treatments);
    }
}