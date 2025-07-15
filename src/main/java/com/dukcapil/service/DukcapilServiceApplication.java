package com.dukcapil.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class DukcapilServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DukcapilServiceApplication.class, args);
        
        System.out.println("");
        System.out.println("🏛️  DUKCAPIL KTP VERIFICATION SERVICE STARTED!");
        System.out.println("🌐 Service URL: http://localhost:8081");
        System.out.println("📊 Health Check: http://localhost:8081/api/dukcapil/health");
        System.out.println("📋 API Docs: http://localhost:8081/api/dukcapil/docs");
        System.out.println("🔍 Main Endpoint: POST http://localhost:8081/api/dukcapil/verify-nik");
        System.out.println("📈 Statistics: http://localhost:8081/api/dukcapil/stats");
        System.out.println("");
        System.out.println("✅ Ready to serve KTP verification requests!");
        System.out.println("🔗 Waiting for connections from Customer Service...");
        System.out.println("");
    }
}