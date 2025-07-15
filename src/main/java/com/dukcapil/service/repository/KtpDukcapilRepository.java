package com.dukcapil.service.repository;

import com.dukcapil.service.model.KtpDukcapil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface KtpDukcapilRepository extends JpaRepository<KtpDukcapil, Long> {
    
    /**
     * Find KTP by NIK - MAIN METHOD
     */
    Optional<KtpDukcapil> findByNik(String nik);
    
    /**
     * Check if NIK exists - MAIN METHOD
     */
    boolean existsByNik(String nik);
    
    /**
     * Find KTP by NIK and exact name match (case-insensitive) - MAIN METHOD
     */
    @Query("SELECT k FROM KtpDukcapil k WHERE k.nik = :nik AND LOWER(k.namaLengkap) = LOWER(:nama)")
    Optional<KtpDukcapil> findByNikAndNama(@Param("nik") String nik, @Param("nama") String nama);
    
    /**
     * Count KTP records by NIK and name (for validation)
     */
    @Query("SELECT COUNT(k) FROM KtpDukcapil k WHERE k.nik = :nik AND LOWER(k.namaLengkap) = LOWER(:nama)")
    Long countByNikAndNama(@Param("nik") String nik, @Param("nama") String nama);
    
    /**
     * Find KTP by name pattern (for search)
     */
    @Query("SELECT k FROM KtpDukcapil k WHERE LOWER(k.namaLengkap) LIKE LOWER(:namePattern)")
    List<KtpDukcapil> findByNamaPattern(@Param("namePattern") String namePattern);
    
    /**
     * Get KTP count by gender (for statistics)
     */
    @Query("SELECT k.jenisKelamin, COUNT(k) FROM KtpDukcapil k GROUP BY k.jenisKelamin")
    List<Object[]> countByGender();
    
    /**
     * Get KTP count by religion (for statistics)
     */
    @Query("SELECT k.agama, COUNT(k) FROM KtpDukcapil k GROUP BY k.agama ORDER BY COUNT(k) DESC")
    List<Object[]> countByReligion();
    
    /**
     * Get recent KTP records (for monitoring)
     */
    @Query("SELECT k FROM KtpDukcapil k ORDER BY k.createdAt DESC")
    List<KtpDukcapil> findRecentRecords();
}