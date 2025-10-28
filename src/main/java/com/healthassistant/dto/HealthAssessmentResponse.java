package com.healthassistant.dto;

import com.healthassistant.model.RiskAssessment;

public class HealthAssessmentResponse {
    private Long assessmentId;
    private String userName;
    private String userEmail;
    private RiskAssessment.RiskLevel riskLevel;
    private Double riskPercentage;
    private String riskFactors;
    private String recommendations;
    private String pdfReportPath;
    private String message;

    public HealthAssessmentResponse() {}

    public HealthAssessmentResponse(String message) {
        this.message = message;
    }

    // Getters and Setters
    public Long getAssessmentId() { return assessmentId; }
    public void setAssessmentId(Long assessmentId) { this.assessmentId = assessmentId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public RiskAssessment.RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskAssessment.RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public Double getRiskPercentage() { return riskPercentage; }
    public void setRiskPercentage(Double riskPercentage) { this.riskPercentage = riskPercentage; }

    public String getRiskFactors() { return riskFactors; }
    public void setRiskFactors(String riskFactors) { this.riskFactors = riskFactors; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }

    public String getPdfReportPath() { return pdfReportPath; }
    public void setPdfReportPath(String pdfReportPath) { this.pdfReportPath = pdfReportPath; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
