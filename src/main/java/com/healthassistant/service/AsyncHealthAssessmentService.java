package com.healthassistant.service;

import com.healthassistant.dto.HealthDataRequest;
import com.healthassistant.dto.HealthAssessmentResponse;
import com.healthassistant.model.*;
import com.healthassistant.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class AsyncHealthAssessmentService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncHealthAssessmentService.class);
    
    // Thread pool for handling concurrent requests
    private final Executor taskExecutor = Executors.newFixedThreadPool(10);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HealthDataRepository healthDataRepository;
    
    @Autowired
    private RiskAssessmentRepository riskAssessmentRepository;
    
    @Autowired
    private MLRiskCalculationService mlRiskCalculationService;
    
    @Autowired
    private PDFReportService pdfReportService;

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<HealthAssessmentResponse> processHealthAssessmentAsync(HealthDataRequest request) {
        try {
            logger.info("Starting async health assessment for user: {}", request.getEmail());
            
            // Step 1: Create or find user
            User user = createOrFindUser(request);
            
            // Step 2: Save health data
            HealthData healthData = saveHealthData(request, user);
            
            // Step 3: Calculate risk using ML model
            RiskAssessment assessment = mlRiskCalculationService.calculateRisk(healthData);
            assessment.setHealthData(healthData);
            
            // Step 4: Generate PDF report
            String pdfPath = pdfReportService.generateHealthReport(assessment);
            assessment.setPdfReportPath(pdfPath);
            
            // Step 5: Save assessment
            RiskAssessment savedAssessment = riskAssessmentRepository.save(assessment);
            
            // Step 6: Create response
            HealthAssessmentResponse response = createResponse(savedAssessment);
            
            logger.info("Completed async health assessment for user: {} with risk level: {}", 
                request.getEmail(), assessment.getRiskLevel());
            
            return CompletableFuture.completedFuture(response);
            
        } catch (Exception e) {
            logger.error("Error processing health assessment for user: {}", request.getEmail(), e);
            HealthAssessmentResponse errorResponse = new HealthAssessmentResponse();
            errorResponse.setMessage("Error processing health assessment: " + e.getMessage());
            return CompletableFuture.completedFuture(errorResponse);
        }
    }

    private User createOrFindUser(HealthDataRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        
        if (user == null) {
            user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setAge(request.getAge());
            user.setGender(User.Gender.valueOf(request.getGender().toUpperCase()));
            user = userRepository.save(user);
            logger.info("Created new user: {}", user.getEmail());
        } else {
            logger.info("Found existing user: {}", user.getEmail());
        }
        
        return user;
    }

    private HealthData saveHealthData(HealthDataRequest request, User user) {
        HealthData healthData = new HealthData();
        healthData.setUser(user);
        healthData.setHeight(request.getHeight());
        healthData.setWeight(request.getWeight());
        healthData.setSystolicBP(request.getSystolicBP());
        healthData.setDiastolicBP(request.getDiastolicBP());
        healthData.setHeartRate(request.getHeartRate());
        healthData.setCholesterol(request.getCholesterol());
        healthData.setBloodSugar(request.getBloodSugar());
        healthData.setHasDiabetes(request.getHasDiabetes());
        healthData.setHasHypertension(request.getHasHypertension());
        healthData.setHasHeartDisease(request.getHasHeartDisease());
        healthData.setSmokes(request.getSmokes());
        healthData.setExercisesRegularly(request.getExercisesRegularly());
        
        return healthDataRepository.save(healthData);
    }

    private HealthAssessmentResponse createResponse(RiskAssessment assessment) {
        HealthAssessmentResponse response = new HealthAssessmentResponse();
        response.setAssessmentId(assessment.getId());
        response.setUserName(assessment.getHealthData().getUser().getName());
        response.setUserEmail(assessment.getHealthData().getUser().getEmail());
        response.setRiskLevel(assessment.getRiskLevel());
        response.setRiskPercentage(assessment.getRiskPercentage());
        response.setRiskFactors(assessment.getRiskFactors());
        response.setRecommendations(assessment.getRecommendations());
        response.setPdfReportPath(assessment.getPdfReportPath());
        response.setMessage("Health assessment completed successfully");
        
        return response;
    }

    // Synchronous method for immediate response (optional)
    @Transactional
    public HealthAssessmentResponse processHealthAssessmentSync(HealthDataRequest request) {
        try {
            logger.info("Processing synchronous health assessment for user: {}", request.getEmail());
            
            User user = createOrFindUser(request);
            HealthData healthData = saveHealthData(request, user);
            RiskAssessment assessment = mlRiskCalculationService.calculateRisk(healthData);
            assessment.setHealthData(healthData);
            
            // Generate PDF in background thread
            CompletableFuture.runAsync(() -> {
                try {
                    String pdfPath = pdfReportService.generateHealthReport(assessment);
                    assessment.setPdfReportPath(pdfPath);
                    riskAssessmentRepository.save(assessment);
                } catch (Exception e) {
                    logger.error("Error generating PDF report", e);
                }
            }, taskExecutor);
            
            RiskAssessment savedAssessment = riskAssessmentRepository.save(assessment);
            HealthAssessmentResponse response = createResponse(savedAssessment);
            
            logger.info("Completed synchronous health assessment for user: {}", request.getEmail());
            return response;
            
        } catch (Exception e) {
            logger.error("Error processing synchronous health assessment for user: {}", request.getEmail(), e);
            HealthAssessmentResponse errorResponse = new HealthAssessmentResponse();
            errorResponse.setMessage("Error processing health assessment: " + e.getMessage());
            return errorResponse;
        }
    }
}
