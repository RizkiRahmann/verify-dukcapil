package com.dukcapil.service.controller;

import com.dukcapil.service.dto.NikVerificationRequest;
import com.dukcapil.service.dto.NikCheckRequest;
import com.dukcapil.service.dto.KtpDataResponse;
import com.dukcapil.service.dto.KtpStatsResponse;
import com.dukcapil.service.service.DukcapilService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/dukcapil")
@CrossOrigin(origins = "*")
public class DukcapilController {
    
    @Autowired
    private DukcapilService dukcapilService;
    
    /**
     * ROOT endpoint untuk testing
     */
    @GetMapping("/")
    public ResponseEntity<?> root() {
        return ResponseEntity.ok(Map.of(
            "service", "Dukcapil KTP Verification Service",
            "version", "1.0.0",
            "status", "Running",
            "timestamp", System.currentTimeMillis(),
            "endpoints", Map.of(
                "health", "GET /dukcapil/health",
                "verify", "POST /dukcapil/verify-nik",
                "docs", "GET /dukcapil/docs"
            )
        ));
    }
    
    /**
     * ENDPOINT UTAMA - Verifikasi NIK dengan nama lengkap
     */
    @PostMapping("/verify-nik")
    public ResponseEntity<?> verifyNik(@Valid @RequestBody NikVerificationRequest request) {
        try {
            System.out.println("📥 Received NIK verification request: " + request);
            
            KtpDataResponse response = dukcapilService.verifyNikAndName(
                request.getNik(), 
                request.getNamaLengkap()
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
            System.err.println("💥 Error in verifyNik endpoint: " + e.getMessage());
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
     * Check NIK existence tanpa validasi nama
     */
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
    
    /**
     * Get KTP data by NIK (untuk admin/debugging)
     */
    @GetMapping("/ktp-data/{nik}")
    public ResponseEntity<?> getKtpData(@PathVariable String nik) {
        try {
            if (nik.length() != 16 || !nik.matches("^[0-9]{16}$")) {
                return ResponseEntity.badRequest().body(Map.of(
                    "found", false,
                    "message", "Format NIK tidak valid. NIK harus 16 digit angka.",
                    "service", "Dukcapil Service"
                ));
            }
            
            var ktpData = dukcapilService.getKtpDataByNik(nik);
            
            if (ktpData.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "found", true,
                    "data", ktpData.get(),
                    "message", "Data KTP ditemukan",
                    "service", "Dukcapil Service",
                    "timestamp", java.time.Instant.now().toString()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "found", false,
                    "message", "Data KTP tidak ditemukan untuk NIK: " + nik,
                    "service", "Dukcapil Service",
                    "timestamp", java.time.Instant.now().toString()
                ));
            }
            
        } catch (Exception e) {
            System.err.println("Error getting KTP data: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "found", false,
                "message", "Terjadi kesalahan: " + e.getMessage(),
                "service", "Dukcapil Service"
            ));
        }
    }
    
    /**
     * Get simple statistics untuk monitoring
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            Map<String, Object> stats = dukcapilService.getSimpleStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            System.err.println("Error getting stats: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Terjadi kesalahan saat mengambil statistik: " + e.getMessage(),
                "service", "Dukcapil Service"
            ));
        }
    }
    
    /**
     * Health check endpoint untuk monitoring
     */
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
                    "verifyNik", "POST /dukcapil/verify-nik",
                    "checkNik", "POST /dukcapil/check-nik",
                    "getKtpData", "GET /dukcapil/ktp-data/{nik}",
                    "stats", "GET /dukcapil/stats",
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
    
    /**
     * Get API documentation
     */
    @GetMapping("/docs")
    public ResponseEntity<?> getApiDocs() {
        return ResponseEntity.ok(Map.of(
            "service", "Dukcapil KTP Verification Service",
            "version", "1.0.0",
            "description", "Service untuk verifikasi data KTP Dukcapil",
            "baseUrl", "http://localhost:8081/api/dukcapil",
            "endpoints", Map.of(
                "POST /dukcapil/verify-nik", Map.of(
                    "description", "Verifikasi NIK dan nama lengkap",
                    "request", Map.of(
                        "nik", "string (16 digit)",
                        "namaLengkap", "string"
                    ),
                    "response", Map.of(
                        "valid", "boolean",
                        "message", "string",
                        "data", "object (jika valid)"
                    )
                ),
                "POST /dukcapil/check-nik", Map.of(
                    "description", "Check keberadaan NIK",
                    "request", Map.of("nik", "string (16 digit)"),
                    "response", Map.of("exists", "boolean", "message", "string")
                ),
                "GET /dukcapil/health", Map.of(
                    "description", "Health check service",
                    "response", Map.of("status", "string", "service", "string")
                )
            ),
            "timestamp", java.time.Instant.now().toString()
        ));
    }
    
    /**
     * Ping endpoint untuk quick check
     */
    @GetMapping("/ping")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok(Map.of(
            "message", "pong",
            "service", "Dukcapil Service",
            "timestamp", java.time.Instant.now().toString()
        ));
    }
}