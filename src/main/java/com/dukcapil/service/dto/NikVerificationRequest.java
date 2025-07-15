package com.dukcapil.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class NikVerificationRequest {
    
    @NotBlank(message = "NIK wajib diisi")
    @Size(min = 16, max = 16, message = "NIK harus 16 digit")
    @Pattern(regexp = "^[0-9]{16}$", message = "NIK hanya boleh berisi angka")
    private String nik;
    
    @NotBlank(message = "Nama lengkap wajib diisi")
    @Size(min = 2, max = 100, message = "Nama lengkap antara 2-100 karakter")
    private String namaLengkap;
    
    // Constructors
    public NikVerificationRequest() {}
    
    public NikVerificationRequest(String nik, String namaLengkap) {
        this.nik = nik;
        this.namaLengkap = namaLengkap;
    }
    
    // Getters and Setters
    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }
    
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    
    @Override
    public String toString() {
        return "NikVerificationRequest{" +
                "nik='" + nik + '\'' +
                ", namaLengkap='" + namaLengkap + '\'' +
                '}';
    }
}