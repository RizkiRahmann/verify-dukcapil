package com.dukcapil.dukcapil.controller;

import com.dukcapil.service.dto.NikVerificationRequest;
import com.dukcapil.service.dto.KtpDataResponse;
import com.dukcapil.service.service.DukcapilService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dukcapil")
@CrossOrigin(origins = "*")
public class DukcapilController {
    
    @Autowired
    private DukcapilService dukcapilService;
    
    /**
     * Verifikasi NIK dengan nama - Endpoint utama untuk Customer Service
     */
    @PostMapping("/verify-nik")
    public ResponseEntity<?> verifyNik(@Valid @RequestBody NikVerificationRequest request) {
        try {
            KtpDataResponse response = dukcapilService.verifyNikAndName(
                request.getNik(), 
                request.getNamaLengkap()
            );
            
            if (response.isValid()) {
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "message", "NIK dan nama valid",
                    "data", response.getData()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "message", response.getMessage()
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "message", "Error verifikasi: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Check NIK existence only
     */
    @PostMapping("/check-nik")
    public ResponseEntity<?> checkNik(@RequestBody Map<String, String> request) {
        try {
            String nik = request.get("nik");
            boolean exists = dukcapilService.isNikExists(nik);
            
            return ResponseEntity.ok(Map.of(
                "exists", exists,
                "nik", nik,
                "message", exists ? "NIK terdaftar" : "NIK tidak terdaftar"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "exists", false,
                "message", "Error: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Get KTP data by NIK only
     */
    @GetMapping("/ktp-data/{nik}")
    public ResponseEntity<?> getKtpData(@PathVariable String nik) {
        try {
            var ktpData = dukcapilService.getKtpDataByNik(nik);
            
            if (ktpData.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "found", true,
                    "data", ktpData.get()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "found", false,
                    "message", "Data KTP tidak ditemukan"
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "found", false,
                "message", "Error: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Health check untuk Dukcapil Service
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "service", "Dukcapil KTP Verification Service",
            "port", 8081,
            "database", "dukcapil_ktp",
            "endpoints", Map.of(
                "verifyNik", "POST /api/dukcapil/verify-nik",
                "checkNik", "POST /api/dukcapil/check-nik",
                "getKtpData", "GET /api/dukcapil/ktp-data/{nik}"
            )
        ));
    }
    
    /**
     * Get statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            return ResponseEntity.ok(dukcapilService.getStats());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error getting stats: " + e.getMessage()
            ));
        }
    }
}