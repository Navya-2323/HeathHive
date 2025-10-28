package com.healthassistant.controller;

import com.healthassistant.dto.HealthAssessmentResponse;
import com.healthassistant.dto.HealthDataRequest;
import com.healthassistant.model.RiskAssessment;
import com.healthassistant.repository.RiskAssessmentRepository;
import com.healthassistant.service.AsyncHealthAssessmentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class HealthAssessmentController {

    private static final Logger logger = LoggerFactory.getLogger(HealthAssessmentController.class);

    @Autowired
    private AsyncHealthAssessmentService healthAssessmentService;
    
    @Autowired
    private RiskAssessmentRepository riskAssessmentRepository;

    @PostMapping("/assess")
    public ResponseEntity<HealthAssessmentResponse> assessHealth(@Valid @RequestBody HealthDataRequest request) {
        try {
            logger.info("Received health assessment request for user: {}", request.getEmail());
            
            // Process assessment asynchronously
            CompletableFuture<HealthAssessmentResponse> future = 
                healthAssessmentService.processHealthAssessmentAsync(request);
            
            // Wait for completion and return response
            HealthAssessmentResponse response = future.get();
            
            if (response.getMessage() != null && response.getMessage().contains("Error")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error in health assessment endpoint", e);
            HealthAssessmentResponse errorResponse = new HealthAssessmentResponse();
            errorResponse.setMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/assess/sync")
    public ResponseEntity<HealthAssessmentResponse> assessHealthSync(@Valid @RequestBody HealthDataRequest request) {
        try {
            logger.info("Received synchronous health assessment request for user: {}", request.getEmail());
            
            HealthAssessmentResponse response = healthAssessmentService.processHealthAssessmentSync(request);
            
            if (response.getMessage() != null && response.getMessage().contains("Error")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error in synchronous health assessment endpoint", e);
            HealthAssessmentResponse errorResponse = new HealthAssessmentResponse();
            errorResponse.setMessage("Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/assessments/{email}")
    public ResponseEntity<List<RiskAssessment>> getUserAssessments(@PathVariable String email) {
        try {
            logger.info("Retrieving assessments for user: {}", email);
            
            List<RiskAssessment> assessments = riskAssessmentRepository.findByHealthDataUserEmail(email);
            
            return ResponseEntity.ok(assessments);
            
        } catch (Exception e) {
            logger.error("Error retrieving assessments for user: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/assessments/{assessmentId}")
    public ResponseEntity<RiskAssessment> getAssessmentById(@PathVariable Long assessmentId) {
        try {
            logger.info("Retrieving assessment with ID: {}", assessmentId);
            
            return riskAssessmentRepository.findById(assessmentId)
                .map(assessment -> ResponseEntity.ok(assessment))
                .orElse(ResponseEntity.notFound().build());
            
        } catch (Exception e) {
            logger.error("Error retrieving assessment with ID: {}", assessmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Health Assistant API is running!");
    }

    @GetMapping("/stats")
    public ResponseEntity<String> getStats() {
        try {
            long totalAssessments = riskAssessmentRepository.count();
            long lowRiskCount = riskAssessmentRepository.count();
            long mediumRiskCount = riskAssessmentRepository.count();
            long highRiskCount = riskAssessmentRepository.count();
            
            // Note: In a real implementation, you'd want to count by risk level
            String stats = String.format(
                "Total Assessments: %d\nLow Risk: %d\nMedium Risk: %d\nHigh Risk: %d",
                totalAssessments, lowRiskCount, mediumRiskCount, highRiskCount
            );
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error retrieving stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving stats");
        }
    }
}
