package com.dukcapil.service.controller;

import com.dukcapil.service.dto.NikVerificationRequest;
import com.dukcapil.service.dto.KtpDataResponse;
import com.dukcapil.service.service.DukcapilService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/dukcapil")
@CrossOrigin(origins = "*")
public class DukcapilController {
    
    @Autowired
    private DukcapilService dukcapilService;
    
    /**
     * ENDPOINT UTAMA - Verifikasi NIK dengan nama lengkap dan tanggal lahir
     */
    @PostMapping("/verify-nik")
    public ResponseEntity<?> verifyNik(@Valid @RequestBody NikVerificationRequest request) {
        try {
            System.out.println("📥 Received enhanced NIK verification request: " + request);
            
            KtpDataResponse response = dukcapilService.verifyNikNameAndBirthDate(
                request.getNik(), 
                request.getNamaLengkap(),
                request.getTanggalLahir()
            );
            
            if (response.isValid()) {
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "message", response.getMessage(),
                    "data", response.getData() != null ? response.getData() : Map.of(),
                    "timestamp", response.getTimestamp(),
                    "service", "Dukcapil Service"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "message", response.getMessage(),
                    "timestamp", response.getTimestamp(),
                    "service", "Dukcapil Service"
                ));
            }
            
        } catch (Exception e) {
            System.err.println("💥 Error in enhanced verifyNik endpoint: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "message", "Terjadi kesalahan sistem: " + e.getMessage(),
                "service", "Dukcapil Service",
                "timestamp", java.time.Instant.now().toString()
            ));
        }
    }
    
    /**
     * ENDPOINT LEGACY - Verifikasi NIK dengan nama saja (untuk backward compatibility)
     */
    @PostMapping("/verify-nik-basic")
    public ResponseEntity<?> verifyNikBasic(@RequestBody Map<String, String> request) {
        try {
            String nik = request.get("nik");
            String namaLengkap = request.get("namaLengkap");
            
            if (nik == null || nik.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", "NIK wajib diisi",
                    "service", "Dukcapil Service"
                ));
            }
            
            if (namaLengkap == null || namaLengkap.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", "Nama lengkap wajib diisi",
                    "service", "Dukcapil Service"
                ));
            }
            
            KtpDataResponse response = dukcapilService.verifyNikAndName(nik, namaLengkap);
            
            if (response.isValid()) {
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "message", response.getMessage(),
                    "data", response.getData() != null ? response.getData() : Map.of(),
                    "timestamp", response.getTimestamp(),
                    "service", "Dukcapil Service"
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "message", response.getMessage(),
                    "timestamp", response.getTimestamp(),
                    "service", "Dukcapil Service"
                ));
            }
            
        } catch (Exception e) {
            System.err.println("Error in basic verification: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "valid", false,
                "message", "Terjadi kesalahan: " + e.getMessage(),
                "service", "Dukcapil Service"
            ));
        }
    }
    
    // Existing endpoints tetap sama...
    @GetMapping("/")
    public ResponseEntity<?> root() {
        return ResponseEntity.ok(Map.of(
            "service", "Dukcapil KTP Verification Service",
            "version", "1.0.0",
            "status", "Running",
            "timestamp", System.currentTimeMillis(),
            "endpoints", Map.of(
                "health", "GET /dukcapil/health",
                "verifyEnhanced", "POST /dukcapil/verify-nik",
                "verifyBasic", "POST /dukcapil/verify-nik-basic",
                "docs", "GET /dukcapil/docs"
            )
        ));
    }
    
    @PostMapping("/check-nik")
    public ResponseEntity<?> checkNik(@RequestBody Map<String, String> request) {
        try {
            String nik = request.get("nik");
            
            if (nik == null || nik.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "exists", false,
                    "message", "NIK wajib diisi",
                    "service", "Dukcapil Service"
                ));
            }
            
            if (nik.length() != 16 || !nik.matches("^[0-9]{16}$")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "exists", false,
                    "message", "Format NIK tidak valid. NIK harus 16 digit angka.",
                    "service", "Dukcapil Service"
                ));
            }
            
            boolean exists = dukcapilService.isNikExists(nik);
            
            return ResponseEntity.ok(Map.of(
                "exists", exists,
                "nik", nik,
                "message", exists ? "NIK terdaftar di database Dukcapil" : "NIK tidak terdaftar",
                "service", "Dukcapil Service",
                "timestamp", java.time.Instant.now().toString()
            ));
            
        } catch (Exception e) {
            System.err.println("Error in checkNik: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "exists", false,
                "message", "Terjadi kesalahan: " + e.getMessage(),
                "service", "Dukcapil Service"
            ));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        try {
            long totalRecords = dukcapilService.getSimpleStats().containsKey("totalKtpRecords") ? 
                (Long) dukcapilService.getSimpleStats().get("totalKtpRecords") : 0;
            
            return ResponseEntity.ok(Map.of(
                "status", "OK",
                "service", "Dukcapil KTP Verification Service",
                "version", "1.0.0",
                "port", 8081,
                "database", "dukcapil_ktp",
                "totalRecords", totalRecords,
                "endpoints", Map.of(
                    "root", "GET /dukcapil/",
                    "verifyNikEnhanced", "POST /dukcapil/verify-nik",
                    "verifyNikBasic", "POST /dukcapil/verify-nik-basic",
                    "checkNik", "POST /dukcapil/check-nik",
                    "health", "GET /dukcapil/health"
                ),
                "timestamp", java.time.Instant.now().toString(),
                "uptime", java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime()
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                "status", "DEGRADED",
                "service", "Dukcapil KTP Verification Service",
                "error", e.getMessage(),
                "timestamp", java.time.Instant.now().toString()
            ));
        }
    }
    
    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok(Map.of(
            "message", "pong",
            "service", "Dukcapil Service",
            "timestamp", java.time.Instant.now().toString()
        ));
    }
}
