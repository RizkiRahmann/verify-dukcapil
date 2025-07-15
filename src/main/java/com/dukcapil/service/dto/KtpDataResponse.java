package com.dukcapil.service.dto;

import java.util.Map;

public class KtpDataResponse {
    private boolean valid;
    private String message;
    private Map<String, Object> data;
    private String timestamp;
    
    public KtpDataResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
        this.data = null;
        this.timestamp = java.time.Instant.now().toString();
    }
    
    public KtpDataResponse(boolean valid, String message, Map<String, Object> data) {
        this.valid = valid;
        this.message = message;
        this.data = data;
        this.timestamp = java.time.Instant.now().toString();
    }
    
    // Getters and Setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    
    @Override
    public String toString() {
        return "KtpDataResponse{" +
                "valid=" + valid +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
