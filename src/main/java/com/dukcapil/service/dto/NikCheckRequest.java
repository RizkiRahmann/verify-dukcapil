package com.dukcapil.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class NikCheckRequest {
    
    @NotBlank(message = "NIK wajib diisi")
    @Size(min = 16, max = 16, message = "NIK harus 16 digit")
    @Pattern(regexp = "^[0-9]{16}$", message = "NIK hanya boleh berisi angka")
    private String nik;
    
    // Constructors
    public NikCheckRequest() {}
    
    public NikCheckRequest(String nik) {
        this.nik = nik;
    }
    
    // Getters and Setters
    public String getNik() { return nik; }
    public void setNik(String nik) { this.nik = nik; }
    
    @Override
    public String toString() {
        return "NikCheckRequest{" +
                "nik='" + nik + '\'' +
                '}';
    }
}
