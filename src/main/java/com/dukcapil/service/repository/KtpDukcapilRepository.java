package com.dukcapil.service.repository;

import com.dukcapil.service.model.KtpDukcapil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface KtpDukcapilRepository extends JpaRepository<KtpDukcapil, Long> {
    
    // Existing methods...
    Optional<KtpDukcapil> findByNik(String nik);
    boolean existsByNik(String nik);
    
    @Query("SELECT k FROM KtpDukcapil k WHERE k.nik = :nik AND LOWER(k.namaLengkap) = LOWER(:nama)")
    Optional<KtpDukcapil> findByNikAndNama(@Param("nik") String nik, @Param("nama") String nama);
    
    // NEW METHOD: Verifikasi NIK, nama, dan tanggal lahir
    @Query("SELECT k FROM KtpDukcapil k WHERE k.nik = :nik AND LOWER(k.namaLengkap) = LOWER(:nama) AND k.tanggalLahir = :tanggalLahir")
    Optional<KtpDukcapil> findByNikAndNamaAndTanggalLahir(
        @Param("nik") String nik, 
        @Param("nama") String nama, 
        @Param("tanggalLahir") LocalDate tanggalLahir
    );
    
    // NEW METHOD: Count untuk validation
    @Query("SELECT COUNT(k) FROM KtpDukcapil k WHERE k.nik = :nik AND LOWER(k.namaLengkap) = LOWER(:nama) AND k.tanggalLahir = :tanggalLahir")
    Long countByNikAndNamaAndTanggalLahir(
        @Param("nik") String nik, 
        @Param("nama") String nama, 
        @Param("tanggalLahir") LocalDate tanggalLahir
    );
    
    // Existing other methods...
    @Query("SELECT k FROM KtpDukcapil k WHERE LOWER(k.namaLengkap) LIKE LOWER(:namePattern)")
    List<KtpDukcapil> findByNamaPattern(@Param("namePattern") String namePattern);
    
    @Query("SELECT k.jenisKelamin, COUNT(k) FROM KtpDukcapil k GROUP BY k.jenisKelamin")
    List<Object[]> countByGender();
    
    @Query("SELECT k.agama, COUNT(k) FROM KtpDukcapil k GROUP BY k.agama ORDER BY COUNT(k) DESC")
    List<Object[]> countByReligion();
}
