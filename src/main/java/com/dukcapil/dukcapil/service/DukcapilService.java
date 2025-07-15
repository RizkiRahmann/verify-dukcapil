package main.java.com.dukcapil.dukcapil.service;

import com.dukcapil.service.dto.KtpDataResponse;
import com.dukcapil.service.model.KtpDukcapil;
import com.dukcapil.service.repository.KtpDukcapilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class DukcapilService {
    
    @Autowired
    private KtpDukcapilRepository ktpRepository;
    
    /**
     * Verifikasi NIK dan nama lengkap
     */
    @Transactional(readOnly = true)
    public KtpDataResponse verifyNikAndName(String nik, String namaLengkap) {
        try {
            // Validasi format NIK
            if (!isValidNikFormat(nik)) {
                return new KtpDataResponse(false, "Format NIK tidak valid");
            }
            
            // Cari berdasarkan NIK dan nama
            Optional<KtpDukcapil> ktpOpt = ktpRepository.findByNikAndNama(nik, namaLengkap);
            
            if (ktpOpt.isPresent()) {
                KtpDukcapil ktpData = ktpOpt.get();
                Map<String, Object> data = convertToMap(ktpData);
                
                return new KtpDataResponse(
                    true, 
                    "Data valid sesuai database Dukcapil", 
                    data
                );
            } else {
                // Check jika NIK ada tapi nama tidak cocok
                Optional<KtpDukcapil> nikExists = ktpRepository.findByNik(nik);
                if (nikExists.isPresent()) {
                    return new KtpDataResponse(
                        false, 
                        "NIK terdaftar namun nama tidak sesuai"
                    );
                } else {
                    return new KtpDataResponse(
                        false, 
                        "NIK tidak terdaftar di database Dukcapil"
                    );
                }
            }
            
        } catch (Exception e) {
            return new KtpDataResponse(
                false, 
                "Error sistem: " + e.getMessage()
            );
        }
    }
    
    /**
     * Check apakah NIK ada di database
     */
    @Transactional(readOnly = true)
    public boolean isNikExists(String nik) {
        return ktpRepository.existsByNik(nik);
    }
    
    /**
     * Get data KTP berdasarkan NIK saja
     */
    @Transactional(readOnly = true)
    public Optional<Map<String, Object>> getKtpDataByNik(String nik) {
        Optional<KtpDukcapil> ktpOpt = ktpRepository.findByNik(nik);
        return ktpOpt.map(this::convertToMap);
    }
    
    /**
     * Validasi format NIK (16 digit)
     */
    private boolean isValidNikFormat(String nik) {
        if (nik == null || nik.length() != 16) {
            return false;
        }
        
        try {
            Long.parseLong(nik);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Convert KTP entity to Map
     */
    private Map<String, Object> convertToMap(KtpDukcapil ktp) {
        Map<String, Object> data = new HashMap<>();
        data.put("nik", ktp.getNik());
        data.put("namaLengkap", ktp.getNamaLengkap());
        data.put("tempatLahir", ktp.getTempatLahir());
        data.put("tanggalLahir", ktp.getTanggalLahir());
        data.put("jenisKelamin", ktp.getJenisKelamin().getValue());
        data.put("alamat", ktp.getNamaAlamat());
        data.put("kecamatan", ktp.getKecamatan());
        data.put("kelurahan", ktp.getKelurahan());
        data.put("agama", ktp.getAgama().getValue());
        data.put("statusPerkawinan", ktp.getStatusPerkawinan());
        
        return data;
    }
    
    /**
     * Get service statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalKtp = ktpRepository.count();
        
        stats.put("totalKtpRecords", totalKtp);
        stats.put("service", "Dukcapil KTP Database");
        stats.put("status", "Active");
        stats.put("timestamp", System.currentTimeMillis());
        
        return stats;
    }
}