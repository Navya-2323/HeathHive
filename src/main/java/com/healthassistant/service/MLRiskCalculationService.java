package com.healthassistant.service;

import com.healthassistant.model.HealthData;
import com.healthassistant.model.RiskAssessment;
import com.healthassistant.model.User;
import org.springframework.stereotype.Service;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import java.util.ArrayList;
import java.util.List;

@Service
public class MLRiskCalculationService {

    // Simplified ML model for health risk assessment
    // In a real-world scenario, this would use a trained model from scikit-learn, TensorFlow, etc.
    
    public RiskAssessment calculateRisk(HealthData healthData) {
        RiskAssessment assessment = new RiskAssessment();
        assessment.setHealthData(healthData);
        
        // Calculate risk percentage using weighted factors
        double riskScore = calculateRiskScore(healthData);
        assessment.setRiskPercentage(Math.min(100.0, Math.max(0.0, riskScore)));
        
        // Determine risk level based on percentage
        RiskAssessment.RiskLevel riskLevel = RiskAssessment.RiskLevel.fromPercentage(riskScore);
        assessment.setRiskLevel(riskLevel);
        
        // Generate risk factors and recommendations
        assessment.setRiskFactors(generateRiskFactors(healthData, riskScore));
        assessment.setRecommendations(generateRecommendations(healthData, riskLevel));
        
        return assessment;
    }
    
    private double calculateRiskScore(HealthData healthData) {
        double riskScore = 0.0;
        
        // Age factor (increases risk with age)
        if (healthData.getUser().getAge() != null) {
            riskScore += healthData.getUser().getAge() * 0.5;
        }
        
        // BMI factor
        Double bmi = healthData.getBMI();
        if (bmi != null) {
            if (bmi < 18.5) {
                riskScore += 5.0; // Underweight
            } else if (bmi > 25 && bmi <= 30) {
                riskScore += 10.0; // Overweight
            } else if (bmi > 30) {
                riskScore += 20.0; // Obese
            }
        }
        
        // Blood pressure factors
        if (healthData.getSystolicBP() != null && healthData.getDiastolicBP() != null) {
            if (healthData.getSystolicBP() > 140 || healthData.getDiastolicBP() > 90) {
                riskScore += 15.0; // Hypertension
            } else if (healthData.getSystolicBP() > 120 || healthData.getDiastolicBP() > 80) {
                riskScore += 8.0; // Pre-hypertension
            }
        }
        
        // Heart rate factor
        if (healthData.getHeartRate() != null) {
            if (healthData.getHeartRate() < 60 || healthData.getHeartRate() > 100) {
                riskScore += 5.0; // Abnormal heart rate
            }
        }
        
        // Cholesterol factor
        if (healthData.getCholesterol() != null) {
            if (healthData.getCholesterol() > 200) {
                riskScore += 12.0; // High cholesterol
            }
        }
        
        // Blood sugar factor
        if (healthData.getBloodSugar() != null) {
            if (healthData.getBloodSugar() > 126) {
                riskScore += 15.0; // High blood sugar (diabetes)
            } else if (healthData.getBloodSugar() > 100) {
                riskScore += 8.0; // Pre-diabetes
            }
        }
        
        // Lifestyle factors
        if (Boolean.TRUE.equals(healthData.getSmokes())) {
            riskScore += 20.0; // Smoking significantly increases risk
        }
        
        if (Boolean.TRUE.equals(healthData.getHasDiabetes())) {
            riskScore += 15.0; // Existing diabetes
        }
        
        if (Boolean.TRUE.equals(healthData.getHasHypertension())) {
            riskScore += 12.0; // Existing hypertension
        }
        
        if (Boolean.TRUE.equals(healthData.getHasHeartDisease())) {
            riskScore += 25.0; // Existing heart disease
        }
        
        // Exercise factor (reduces risk)
        if (Boolean.TRUE.equals(healthData.getExercisesRegularly())) {
            riskScore -= 8.0; // Regular exercise reduces risk
        }
        
        // Gender factor (males generally have higher cardiovascular risk)
        if (healthData.getUser().getGender() == User.Gender.MALE) {
            riskScore += 3.0;
        }
        
        return Math.max(0.0, riskScore);
    }
    
    private String generateRiskFactors(HealthData healthData, double riskScore) {
        List<String> factors = new ArrayList<>();
        
        // Age factor
        if (healthData.getUser().getAge() != null && healthData.getUser().getAge() > 65) {
            factors.add("Advanced age");
        }
        
        // BMI factor
        Double bmi = healthData.getBMI();
        if (bmi != null) {
            if (bmi < 18.5) {
                factors.add("Underweight (BMI: " + String.format("%.1f", bmi) + ")");
            } else if (bmi > 25) {
                factors.add("High BMI (BMI: " + String.format("%.1f", bmi) + ")");
            }
        }
        
        // Blood pressure
        if (healthData.getSystolicBP() != null && healthData.getDiastolicBP() != null) {
            if (healthData.getSystolicBP() > 140 || healthData.getDiastolicBP() > 90) {
                factors.add("Hypertension (" + healthData.getSystolicBP() + "/" + healthData.getDiastolicBP() + " mmHg)");
            }
        }
        
        // Cholesterol
        if (healthData.getCholesterol() != null && healthData.getCholesterol() > 200) {
            factors.add("High cholesterol (" + healthData.getCholesterol() + " mg/dL)");
        }
        
        // Blood sugar
        if (healthData.getBloodSugar() != null && healthData.getBloodSugar() > 126) {
            factors.add("High blood sugar (" + healthData.getBloodSugar() + " mg/dL)");
        }
        
        // Lifestyle factors
        if (Boolean.TRUE.equals(healthData.getSmokes())) {
            factors.add("Smoking");
        }
        
        if (Boolean.TRUE.equals(healthData.getHasDiabetes())) {
            factors.add("Diabetes");
        }
        
        if (Boolean.TRUE.equals(healthData.getHasHypertension())) {
            factors.add("Hypertension");
        }
        
        if (Boolean.TRUE.equals(healthData.getHasHeartDisease())) {
            factors.add("Heart disease");
        }
        
        if (factors.isEmpty()) {
            factors.add("No significant risk factors identified");
        }
        
        return String.join(", ", factors);
    }
    
    private String generateRecommendations(HealthData healthData, RiskAssessment.RiskLevel riskLevel) {
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
        Double bmi = healthData.getBMI();
        if (bmi != null && bmi > 25) {
            recommendations.add("Consider weight management program");
            recommendations.add("Increase physical activity");
        }
        
        if (healthData.getSystolicBP() != null && healthData.getSystolicBP() > 120) {
            recommendations.add("Monitor blood pressure regularly");
            recommendations.add("Consider dietary changes to reduce sodium intake");
        }
        
        if (healthData.getCholesterol() != null && healthData.getCholesterol() > 200) {
            recommendations.add("Consider dietary changes to reduce cholesterol");
            recommendations.add("Discuss cholesterol management with healthcare provider");
        }
        
        if (Boolean.TRUE.equals(healthData.getSmokes())) {
            recommendations.add("Consider smoking cessation programs");
            recommendations.add("Seek support for quitting smoking");
        }
        
        if (Boolean.FALSE.equals(healthData.getExercisesRegularly())) {
            recommendations.add("Start regular exercise routine");
            recommendations.add("Aim for at least 150 minutes of moderate exercise per week");
        }
        
        return String.join("; ", recommendations);
    }
}
