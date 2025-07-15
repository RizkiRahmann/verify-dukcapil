package com.dukcapil.service.service;

import com.dukcapil.service.dto.KtpDataResponse;
import com.dukcapil.service.dto.KtpStatsResponse;
import com.dukcapil.service.model.KtpDukcapil;
import com.dukcapil.service.repository.KtpDukcapilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DukcapilService {
    
    @Autowired
    private KtpDukcapilRepository ktpRepository;
    
    /**
     * Verifikasi NIK dan nama lengkap - METHOD UTAMA
     */
    @Transactional(readOnly = true)
    public KtpDataResponse verifyNikAndName(String nik, String namaLengkap) {
        try {
            // Log untuk audit trail
            System.out.println("🔍 Verifying NIK: " + nik + " with name: " + namaLengkap);
            
            // Validasi format NIK
            if (!isValidNikFormat(nik)) {
                return new KtpDataResponse(false, "Format NIK tidak valid. NIK harus 16 digit angka.");
            }
            
            // Validasi nama tidak kosong
            if (namaLengkap == null || namaLengkap.trim().isEmpty()) {
                return new KtpDataResponse(false, "Nama lengkap tidak boleh kosong.");
            }
            
            // Cari berdasarkan NIK dan nama (case-insensitive)
            Optional<KtpDukcapil> ktpOpt = ktpRepository.findByNikAndNama(nik, namaLengkap.trim());
            
            if (ktpOpt.isPresent()) {
                KtpDukcapil ktpData = ktpOpt.get();
                Map<String, Object> data = convertKtpToMap(ktpData);
                
                System.out.println("✅ Verification SUCCESS for NIK: " + nik);
                return new KtpDataResponse(
                    true, 
                    "Data NIK dan nama valid sesuai database Dukcapil", 
                    data
                );
            } else {
                // Check jika NIK ada tapi nama tidak cocok
                Optional<KtpDukcapil> nikExists = ktpRepository.findByNik(nik);
                if (nikExists.isPresent()) {
                    System.out.println("❌ Verification FAILED - Name mismatch for NIK: " + nik);
                    return new KtpDataResponse(
                        false, 
                        "NIK terdaftar namun nama tidak sesuai dengan data Dukcapil. " +
                        "Nama di database: " + nikExists.get().getNamaLengkap()
                    );
                } else {
                    System.out.println("❌ Verification FAILED - NIK not found: " + nik);
                    return new KtpDataResponse(
                        false, 
                        "NIK tidak terdaftar di database Dukcapil"
                    );
                }
            }
            
        } catch (Exception e) {
            System.err.println("💥 Error verifying NIK: " + nik + " - " + e.getMessage());
            return new KtpDataResponse(
                false, 
                "Terjadi kesalahan sistem saat verifikasi: " + e.getMessage()
            );
        }
    }
    
    /**
     * Check apakah NIK ada di database (tanpa validasi nama)
     */
    @Transactional(readOnly = true)
    public boolean isNikExists(String nik) {
        try {
            if (!isValidNikFormat(nik)) {
                return false;
            }
            return ktpRepository.existsByNik(nik);
        } catch (Exception e) {
            System.err.println("Error checking NIK existence: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get data KTP berdasarkan NIK saja (untuk debugging/admin)
     */
    @Transactional(readOnly = true)
    public Optional<Map<String, Object>> getKtpDataByNik(String nik) {
        try {
            if (!isValidNikFormat(nik)) {
                return Optional.empty();
            }
            
            Optional<KtpDukcapil> ktpOpt = ktpRepository.findByNik(nik);
            return ktpOpt.map(this::convertKtpToMap);
        } catch (Exception e) {
            System.err.println("Error getting KTP data: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Get comprehensive statistics
     */
    @Transactional(readOnly = true)
    public KtpStatsResponse getComprehensiveStats() {
        try {
            KtpStatsResponse stats = new KtpStatsResponse();
            
            // Total records
            stats.setTotalRecords(ktpRepository.count());
            
            // Gender distribution
            List<Object[]> genderData = ktpRepository.countByGender();
            Map<String, Long> genderMap = genderData.stream()
                .collect(Collectors.toMap(
                    arr -> ((KtpDukcapil.JenisKelamin) arr[0]).getValue(),
                    arr -> (Long) arr[1]
                ));
            stats.setGenderDistribution(genderMap);
            
            // Religion distribution
            List<Object[]> religionData = ktpRepository.countByReligion();
            Map<String, Long> religionMap = religionData.stream()
                .collect(Collectors.toMap(
                    arr -> ((KtpDukcapil.Agama) arr[0]).getValue(),
                    arr -> (Long) arr[1]
                ));
            stats.setReligionDistribution(religionMap);
            
            return stats;
        } catch (Exception e) {
            System.err.println("Error getting comprehensive stats: " + e.getMessage());
            return new KtpStatsResponse();
        }
    }
    
    /**
     * Get simple statistics untuk dashboard
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSimpleStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            stats.put("totalKtpRecords", ktpRepository.count());
            stats.put("service", "Dukcapil KTP Verification Service");
            stats.put("status", "Active");
            stats.put("timestamp", System.currentTimeMillis());
            stats.put("version", "1.0.0");
            
            // Quick counts
            List<Object[]> genderData = ktpRepository.countByGender();
            for (Object[] row : genderData) {
                KtpDukcapil.JenisKelamin gender = (KtpDukcapil.JenisKelamin) row[0];
                Long count = (Long) row[1];
                stats.put("total" + gender.getValue().replace("-", ""), count);
            }
            
        } catch (Exception e) {
            stats.put("error", "Error getting stats: " + e.getMessage());
            stats.put("status", "Error");
        }
        
        return stats;
    }
    
    /**
     * Search KTP by name pattern (untuk admin/debugging)
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> searchByName(String namePattern) {
        try {
            List<KtpDukcapil> results = ktpRepository.findByNamaPattern("%" + namePattern + "%");
            return results.stream()
                .limit(10) // Batasi hasil
                .map(this::convertKtpToMap)
                .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error searching by name: " + e.getMessage());
            return List.of();
        }
    }
    
    // ===== PRIVATE HELPER METHODS =====
    
    /**
     * Validasi format NIK (16 digit angka)
     */
    private boolean isValidNikFormat(String nik) {
        if (nik == null || nik.length() != 16) {
            return false;
        }
        
        try {
            Long.parseLong(nik);
            
            // Validasi kode wilayah tidak boleh 00
            String provinsi = nik.substring(0, 2);
            String kabupaten = nik.substring(2, 4);
            String kecamatan = nik.substring(4, 6);
            
            return !provinsi.equals("00") && !kabupaten.equals("00") && !kecamatan.equals("00");
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Convert KTP entity ke Map untuk response
     */
    private Map<String, Object> convertKtpToMap(KtpDukcapil ktp) {
        Map<String, Object> data = new HashMap<>();
        data.put("nik", ktp.getNik());
        data.put("namaLengkap", ktp.getNamaLengkap());
        data.put("tempatLahir", ktp.getTempatLahir());
        data.put("tanggalLahir", ktp.getTanggalLahir().toString());
        data.put("jenisKelamin", ktp.getJenisKelamin().getValue());
        data.put("alamat", ktp.getNamaAlamat());
        data.put("kecamatan", ktp.getKecamatan());
        data.put("kelurahan", ktp.getKelurahan());
        data.put("agama", ktp.getAgama().getValue());
        data.put("statusPerkawinan", ktp.getStatusPerkawinan());
        data.put("kewarganegaraan", ktp.getKewarganegaraan());
        data.put("berlakuHingga", ktp.getBerlakuHingga());
        
        return data;
    }
}