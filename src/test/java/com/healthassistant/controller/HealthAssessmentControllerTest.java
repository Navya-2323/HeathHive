package com.healthassistant.controller;

import com.healthassistant.dto.HealthDataRequest;
import com.healthassistant.dto.HealthAssessmentResponse;
import com.healthassistant.service.AsyncHealthAssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class HealthAssessmentControllerTest {

    @Autowired
    private AsyncHealthAssessmentService healthAssessmentService;

    @Test
    public void testHealthAssessment() {
        // Create test health data
        HealthDataRequest request = new HealthDataRequest();
        request.setName("John Doe");
        request.setEmail("john.doe@example.com");
        request.setAge(35);
        request.setGender("MALE");
        request.setHeight(175.0);
        request.setWeight(80.0);
        request.setSystolicBP(120);
        request.setDiastolicBP(80);
        request.setHeartRate(75);
        request.setCholesterol(180.0);
        request.setBloodSugar(95.0);
        request.setHasDiabetes(false);
        request.setHasHypertension(false);
        request.setHasHeartDisease(false);
        request.setSmokes(false);
        request.setExercisesRegularly(true);

        try {
            // Test synchronous processing
            HealthAssessmentResponse response = healthAssessmentService.processHealthAssessmentSync(request);
            
            assertNotNull(response);
            assertNotNull(response.getRiskLevel());
            assertNotNull(response.getRiskPercentage());
            assertTrue(response.getRiskPercentage() >= 0.0 && response.getRiskPercentage() <= 100.0);
            assertNotNull(response.getRiskFactors());
            assertNotNull(response.getRecommendations());
            
            System.out.println("Test passed! Risk Level: " + response.getRiskLevel());
            System.out.println("Risk Percentage: " + response.getRiskPercentage() + "%");
            System.out.println("Risk Factors: " + response.getRiskFactors());
            System.out.println("Recommendations: " + response.getRecommendations());
            
        } catch (Exception e) {
            fail("Health assessment test failed: " + e.getMessage());
        }
    }
}
