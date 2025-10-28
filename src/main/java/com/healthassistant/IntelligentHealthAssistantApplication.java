package com.healthassistant;

import com.healthassistant.dto.HealthDataRequest;
import com.healthassistant.dto.HealthAssessmentResponse;
import com.healthassistant.service.AsyncHealthAssessmentService;
import com.healthassistant.util.SampleDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IntelligentHealthAssistantApplication implements CommandLineRunner {

    @Autowired
    private AsyncHealthAssessmentService healthAssessmentService;

    public static void main(String[] args) {
        System.out.println("Starting Intelligent Health Assistant...");
        SpringApplication.run(IntelligentHealthAssistantApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=== Intelligent Health Assistant Demo ===");
        System.out.println("API Documentation: http://localhost:8080/api/health/health");
        System.out.println("H2 Database Console: http://localhost:8080/h2-console");
        System.out.println("\nRunning sample assessments...\n");

        // Test different risk levels
        testAssessment("Low Risk Sample", SampleDataGenerator.createLowRiskSample());
        testAssessment("Medium Risk Sample", SampleDataGenerator.createMediumRiskSample());
        testAssessment("High Risk Sample", SampleDataGenerator.createHighRiskSample());

        System.out.println("\n=== Demo Complete ===");
        System.out.println("You can now test the API endpoints or check the H2 console for data.");
    }

    private void testAssessment(String testName, HealthDataRequest request) {
        try {
            System.out.println("Testing: " + testName);
            HealthAssessmentResponse response = healthAssessmentService.processHealthAssessmentSync(request);
            
            System.out.println("  Name: " + response.getUserName());
            System.out.println("  Risk Level: " + response.getRiskLevel());
            System.out.println("  Risk Percentage: " + String.format("%.1f", response.getRiskPercentage()) + "%");
            System.out.println("  Risk Factors: " + response.getRiskFactors());
            System.out.println("  PDF Report: " + (response.getPdfReportPath() != null ? "Generated" : "Not generated"));
            System.out.println();
            
        } catch (Exception e) {
            System.err.println("Error testing " + testName + ": " + e.getMessage());
        }
    }
}