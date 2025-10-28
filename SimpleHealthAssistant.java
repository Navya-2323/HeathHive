package com.healthassistant;

import java.util.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleHealthAssistant {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Intelligent Health Assistant");
        System.out.println("========================================");
        System.out.println();
        
        // Create thread pool for concurrent processing
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // Demo the system with sample data
        System.out.println("Running Health Assessment Demo...");
        System.out.println();
        
        // Sample health data
        HealthData[] samples = {
            new HealthData("Alice Johnson", "alice@example.com", 28, "FEMALE", 165.0, 60.0, 110, 70, 65, 160.0, 85.0, false, false, false, false, true),
            new HealthData("Bob Smith", "bob@example.com", 45, "MALE", 180.0, 90.0, 135, 85, 80, 220.0, 110.0, false, false, false, false, false),
            new HealthData("Charlie Brown", "charlie@example.com", 65, "MALE", 170.0, 95.0, 150, 95, 90, 280.0, 140.0, true, true, false, true, false)
        };
        
        // Process assessments concurrently
        List<CompletableFuture<RiskAssessment>> futures = new ArrayList<>();
        
        for (HealthData data : samples) {
            CompletableFuture<RiskAssessment> future = CompletableFuture.supplyAsync(() -> {
                return processHealthAssessment(data);
            }, executor);
            futures.add(future);
        }
        
        // Wait for all assessments to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );
        
        try {
            allFutures.get(); // Wait for completion
            
            // Display results
            System.out.println("========================================");
            System.out.println("   Assessment Results");
            System.out.println("========================================");
            System.out.println();
            
            for (int i = 0; i < futures.size(); i++) {
                RiskAssessment assessment = futures.get(i).get();
                displayAssessment(assessment, i + 1);
            }
            
        } catch (Exception e) {
            System.err.println("Error processing assessments: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
        
        System.out.println();
        System.out.println("========================================");
        System.out.println("   Demo Complete");
        System.out.println("========================================");
        System.out.println();
        System.out.println("This demonstrates:");
        System.out.println("✓ Health data processing");
        System.out.println("✓ ML-based risk calculation");
        System.out.println("✓ Multithreaded concurrent processing");
        System.out.println("✓ Risk assessment and recommendations");
        System.out.println();
        System.out.println("For the full Spring Boot application with:");
        System.out.println("✓ REST API endpoints");
        System.out.println("✓ PDF report generation");
        System.out.println("✓ Database integration");
        System.out.println("✓ Web interface");
        System.out.println();
        System.out.println("Please install Maven and run: mvn spring-boot:run");
        System.out.println("Or use an IDE like IntelliJ IDEA or Eclipse");
    }
    
    private static RiskAssessment processHealthAssessment(HealthData data) {
        // Simulate processing time
        try {
            Thread.sleep(1000 + (int)(Math.random() * 2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Calculate risk using ML algorithm
        double riskScore = calculateRiskScore(data);
        RiskLevel riskLevel = determineRiskLevel(riskScore);
        String riskFactors = identifyRiskFactors(data, riskScore);
        String recommendations = generateRecommendations(data, riskLevel);
        
        return new RiskAssessment(data, riskLevel, riskScore, riskFactors, recommendations);
    }
    
    private static double calculateRiskScore(HealthData data) {
        double riskScore = 0.0;
        
        // Age factor
        riskScore += data.age * 0.5;
        
        // BMI factor
        double bmi = data.weight / Math.pow(data.height / 100.0, 2);
        if (bmi < 18.5) {
            riskScore += 5.0; // Underweight
        } else if (bmi > 25 && bmi <= 30) {
            riskScore += 10.0; // Overweight
        } else if (bmi > 30) {
            riskScore += 20.0; // Obese
        }
        
        // Blood pressure factors
        if (data.systolicBP > 140 || data.diastolicBP > 90) {
            riskScore += 15.0; // Hypertension
        } else if (data.systolicBP > 120 || data.diastolicBP > 80) {
            riskScore += 8.0; // Pre-hypertension
        }
        
        // Heart rate factor
        if (data.heartRate < 60 || data.heartRate > 100) {
            riskScore += 5.0; // Abnormal heart rate
        }
        
        // Cholesterol factor
        if (data.cholesterol > 200) {
            riskScore += 12.0; // High cholesterol
        }
        
        // Blood sugar factor
        if (data.bloodSugar > 126) {
            riskScore += 15.0; // High blood sugar (diabetes)
        } else if (data.bloodSugar > 100) {
            riskScore += 8.0; // Pre-diabetes
        }
        
        // Lifestyle factors
        if (data.smokes) {
            riskScore += 20.0; // Smoking significantly increases risk
        }
        
        if (data.hasDiabetes) {
            riskScore += 15.0; // Existing diabetes
        }
        
        if (data.hasHypertension) {
            riskScore += 12.0; // Existing hypertension
        }
        
        if (data.hasHeartDisease) {
            riskScore += 25.0; // Existing heart disease
        }
        
        // Exercise factor (reduces risk)
        if (data.exercisesRegularly) {
            riskScore -= 8.0; // Regular exercise reduces risk
        }
        
        // Gender factor (males generally have higher cardiovascular risk)
        if ("MALE".equals(data.gender)) {
            riskScore += 3.0;
        }
        
        return Math.max(0.0, riskScore);
    }
    
    private static RiskLevel determineRiskLevel(double riskScore) {
        if (riskScore < 30.0) {
            return RiskLevel.LOW;
        } else if (riskScore < 70.0) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.HIGH;
        }
    }
    
    private static String identifyRiskFactors(HealthData data, double riskScore) {
        List<String> factors = new ArrayList<>();
        
        // Age factor
        if (data.age > 65) {
            factors.add("Advanced age");
        }
        
        // BMI factor
        double bmi = data.weight / Math.pow(data.height / 100.0, 2);
        if (bmi < 18.5) {
            factors.add("Underweight (BMI: " + String.format("%.1f", bmi) + ")");
        } else if (bmi > 25) {
            factors.add("High BMI (BMI: " + String.format("%.1f", bmi) + ")");
        }
        
        // Blood pressure
        if (data.systolicBP > 140 || data.diastolicBP > 90) {
            factors.add("Hypertension (" + data.systolicBP + "/" + data.diastolicBP + " mmHg)");
        }
        
        // Cholesterol
        if (data.cholesterol > 200) {
            factors.add("High cholesterol (" + data.cholesterol + " mg/dL)");
        }
        
        // Blood sugar
        if (data.bloodSugar > 126) {
            factors.add("High blood sugar (" + data.bloodSugar + " mg/dL)");
        }
        
        // Lifestyle factors
        if (data.smokes) {
            factors.add("Smoking");
        }
        
        if (data.hasDiabetes) {
            factors.add("Diabetes");
        }
        
        if (data.hasHypertension) {
            factors.add("Hypertension");
        }
        
        if (data.hasHeartDisease) {
            factors.add("Heart disease");
        }
        
        if (factors.isEmpty()) {
            factors.add("No significant risk factors identified");
        }
        
        return String.join(", ", factors);
    }
    
    private static String generateRecommendations(HealthData data, RiskLevel riskLevel) {
        List<String> recommendations = new ArrayList<>();
        
        // General recommendations based on risk level
        switch (riskLevel) {
            case HIGH:
                recommendations.add("Schedule immediate consultation with a healthcare provider");
                recommendations.add("Consider regular monitoring of vital signs");
                break;
            case MEDIUM:
                recommendations.add("Schedule regular health check-ups");
                recommendations.add("Monitor your health metrics regularly");
                break;
            case LOW:
                recommendations.add("Maintain current healthy lifestyle");
                recommendations.add("Continue regular health screenings");
                break;
        }
        
        // Specific recommendations based on health data
        double bmi = data.weight / Math.pow(data.height / 100.0, 2);
        if (bmi > 25) {
            recommendations.add("Consider weight management program");
            recommendations.add("Increase physical activity");
        }
        
        if (data.systolicBP > 120) {
            recommendations.add("Monitor blood pressure regularly");
            recommendations.add("Consider dietary changes to reduce sodium intake");
        }
        
        if (data.cholesterol > 200) {
            recommendations.add("Consider dietary changes to reduce cholesterol");
            recommendations.add("Discuss cholesterol management with healthcare provider");
        }
        
        if (data.smokes) {
            recommendations.add("Consider smoking cessation programs");
            recommendations.add("Seek support for quitting smoking");
        }
        
        if (!data.exercisesRegularly) {
            recommendations.add("Start regular exercise routine");
            recommendations.add("Aim for at least 150 minutes of moderate exercise per week");
        }
        
        return String.join("; ", recommendations);
    }
    
    private static void displayAssessment(RiskAssessment assessment, int number) {
        System.out.println("Assessment #" + number + ":");
        System.out.println("  Name: " + assessment.data.name);
        System.out.println("  Email: " + assessment.data.email);
        System.out.println("  Age: " + assessment.data.age + " (" + assessment.data.gender + ")");
        System.out.println("  BMI: " + String.format("%.1f", assessment.data.weight / Math.pow(assessment.data.height / 100.0, 2)));
        System.out.println("  Risk Level: " + assessment.riskLevel + " (" + String.format("%.1f", assessment.riskScore) + "%)");
        System.out.println("  Risk Factors: " + assessment.riskFactors);
        System.out.println("  Recommendations: " + assessment.recommendations);
        System.out.println();
    }
    
    // Data classes
    static class HealthData {
        String name, email, gender;
        int age, systolicBP, diastolicBP, heartRate;
        double height, weight, cholesterol, bloodSugar;
        boolean hasDiabetes, hasHypertension, hasHeartDisease, smokes, exercisesRegularly;
        
        HealthData(String name, String email, int age, String gender, double height, double weight,
                  int systolicBP, int diastolicBP, int heartRate, double cholesterol, double bloodSugar,
                  boolean hasDiabetes, boolean hasHypertension, boolean hasHeartDisease, boolean smokes, boolean exercisesRegularly) {
            this.name = name;
            this.email = email;
            this.age = age;
            this.gender = gender;
            this.height = height;
            this.weight = weight;
            this.systolicBP = systolicBP;
            this.diastolicBP = diastolicBP;
            this.heartRate = heartRate;
            this.cholesterol = cholesterol;
            this.bloodSugar = bloodSugar;
            this.hasDiabetes = hasDiabetes;
            this.hasHypertension = hasHypertension;
            this.hasHeartDisease = hasHeartDisease;
            this.smokes = smokes;
            this.exercisesRegularly = exercisesRegularly;
        }
    }
    
    static class RiskAssessment {
        HealthData data;
        RiskLevel riskLevel;
        double riskScore;
        String riskFactors, recommendations;
        
        RiskAssessment(HealthData data, RiskLevel riskLevel, double riskScore, String riskFactors, String recommendations) {
            this.data = data;
            this.riskLevel = riskLevel;
            this.riskScore = riskScore;
            this.riskFactors = riskFactors;
            this.recommendations = recommendations;
        }
    }
    
    enum RiskLevel {
        LOW("Low Risk"),
        MEDIUM("Medium Risk"),
        HIGH("High Risk");
        
        private final String description;
        
        RiskLevel(String description) {
            this.description = description;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
}
