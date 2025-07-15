package com.dukcapil.service.dto;

import java.util.Map;

public class KtpStatsResponse {
    private long totalRecords;
    private Map<String, Long> genderDistribution;
    private Map<String, Long> religionDistribution;
    private Map<String, Long> provinceDistribution;
    private String lastUpdated;
    
    public KtpStatsResponse() {
        this.lastUpdated = java.time.Instant.now().toString();
    }
    
    // Getters and Setters
    public long getTotalRecords() { return totalRecords; }
    public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }
    
    public Map<String, Long> getGenderDistribution() { return genderDistribution; }
    public void setGenderDistribution(Map<String, Long> genderDistribution) { this.genderDistribution = genderDistribution; }
    
    public Map<String, Long> getReligionDistribution() { return religionDistribution; }
    public void setReligionDistribution(Map<String, Long> religionDistribution) { this.religionDistribution = religionDistribution; }
    
    public Map<String, Long> getProvinceDistribution() { return provinceDistribution; }
    public void setProvinceDistribution(Map<String, Long> provinceDistribution) { this.provinceDistribution = provinceDistribution; }
    
    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}