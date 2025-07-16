package com.dukcapil.service.service;

import com.dukcapil.service.dto.KtpDataResponse;
import com.dukcapil.service.model.KtpDukcapil;
import com.dukcapil.service.repository.KtpDukcapilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class DukcapilService {
    
    @Autowired
    private KtpDukcapilRepository ktpRepository;
    
    // EXISTING METHOD - tetap ada untuk backward compatibility
    @Transactional(readOnly = true)
    public KtpDataResponse verifyNikAndName(String nik, String namaLengkap) {
        try {
            System.out.println("🔍 Verifying NIK: " + nik + " with name: " + namaLengkap);
            
            if (!isValidNikFormat(nik)) {
                return new KtpDataResponse(false, "Format NIK tidak valid. NIK harus 16 digit angka.");
            }
            
            if (namaLengkap == null || namaLengkap.trim().isEmpty()) {
                return new KtpDataResponse(false, "Nama lengkap tidak boleh kosong.");
            }
            
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
                Optional<KtpDukcapil> nikExists = ktpRepository.findByNik(nik);
                if (nikExists.isPresent()) {
                    System.out.println("❌ Verification FAILED - Name mismatch for NIK: " + nik);
                    return new KtpDataResponse(
                        false, 
                        "NIK terdaftar namun nama tidak sesuai dengan data Dukcapil"
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
    
    // NEW METHOD: Verifikasi dengan tanggal lahir
    @Transactional(readOnly = true)
    public KtpDataResponse verifyNikNameAndBirthDate(String nik, String namaLengkap, LocalDate tanggalLahir) {
        try {
            System.out.println("🔍 Enhanced Verification - NIK: " + nik + ", Name: " + namaLengkap + ", Birth: " + tanggalLahir);
            
            // Validasi format NIK
            if (!isValidNikFormat(nik)) {
                return new KtpDataResponse(false, "Format NIK tidak valid. NIK harus 16 digit angka.");
            }
            
            // Validasi nama
            if (namaLengkap == null || namaLengkap.trim().isEmpty()) {
                return new KtpDataResponse(false, "Nama lengkap tidak boleh kosong.");
            }
            
            // Validasi tanggal lahir
            if (tanggalLahir == null) {
                return new KtpDataResponse(false, "Tanggal lahir tidak boleh kosong.");
            }
            
            if (tanggalLahir.isAfter(LocalDate.now())) {
                return new KtpDataResponse(false, "Tanggal lahir tidak boleh di masa depan.");
            }
            
            // Cari berdasarkan NIK, nama, dan tanggal lahir
            Optional<KtpDukcapil> ktpOpt = ktpRepository.findByNikAndNamaAndTanggalLahir(
                nik, namaLengkap.trim(), tanggalLahir
            );
            
            if (ktpOpt.isPresent()) {
                KtpDukcapil ktpData = ktpOpt.get();
                Map<String, Object> data = convertKtpToMap(ktpData);
                
                System.out.println("✅ Enhanced Verification SUCCESS for NIK: " + nik);
                return new KtpDataResponse(
                    true, 
                    "Data NIK, nama, dan tanggal lahir valid sesuai database Dukcapil", 
                    data
                );
            } else {
                // Check detail mismatch
                Optional<KtpDukcapil> nikExists = ktpRepository.findByNik(nik);
                if (nikExists.isPresent()) {
                    KtpDukcapil existing = nikExists.get();
                    
                    // Check apakah nama cocok tapi tanggal lahir tidak
                    if (existing.getNamaLengkap().toLowerCase().equals(namaLengkap.trim().toLowerCase())) {
                        return new KtpDataResponse(
                            false, 
                            "NIK dan nama sesuai, namun tanggal lahir tidak cocok. " +
                            "Tanggal lahir di database: " + existing.getTanggalLahir()
                        );
                    } 
                    // Check apakah tanggal lahir cocok tapi nama tidak
                    else if (existing.getTanggalLahir().equals(tanggalLahir)) {
                        return new KtpDataResponse(
                            false, 
                            "NIK dan tanggal lahir sesuai, namun nama tidak cocok. " +
                            "Nama di database: " + existing.getNamaLengkap()
                        );
                    } 
                    // Keduanya tidak cocok
                    else {
                        return new KtpDataResponse(
                            false, 
                            "NIK terdaftar namun nama dan tanggal lahir tidak sesuai dengan data Dukcapil"
                        );
                    }
                } else {
                    return new KtpDataResponse(
                        false, 
                        "NIK tidak terdaftar di database Dukcapil"
                    );
                }
            }
            
        } catch (Exception e) {
            System.err.println("💥 Error in enhanced verification: " + e.getMessage());
            return new KtpDataResponse(
                false, 
                "Terjadi kesalahan sistem saat verifikasi: " + e.getMessage()
            );
        }
    }
    
    // Existing helper methods...
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
    
    @Transactional(readOnly = true)
    public Map<String, Object> getSimpleStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            stats.put("totalKtpRecords", ktpRepository.count());
            stats.put("service", "Dukcapil KTP Verification Service");
            stats.put("status", "Active");
            stats.put("timestamp", System.currentTimeMillis());
            stats.put("version", "1.0.0");
            
        } catch (Exception e) {
            stats.put("error", "Error getting stats: " + e.getMessage());
            stats.put("status", "Error");
        }
        
        return stats;
    }
    
    // Private helper methods
    private boolean isValidNikFormat(String nik) {
        if (nik == null || nik.length() != 16) {
            return false;
        }
        
        try {
            Long.parseLong(nik);
            
            String provinsi = nik.substring(0, 2);
            String kabupaten = nik.substring(2, 4);
            String kecamatan = nik.substring(4, 6);
            
            return !provinsi.equals("00") && !kabupaten.equals("00") && !kecamatan.equals("00");
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
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
