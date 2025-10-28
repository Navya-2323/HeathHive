package com.healthassistant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_assessments")
public class RiskAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_data_id", nullable = false)
    private HealthData healthData;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false)
    private RiskLevel riskLevel;

    @Column(name = "risk_percentage", nullable = false)
    private Double riskPercentage;

    @Column(name = "risk_factors", columnDefinition = "TEXT")
    private String riskFactors;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "pdf_report_path")
    private String pdfReportPath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public RiskAssessment() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public HealthData getHealthData() { return healthData; }
    public void setHealthData(HealthData healthData) { this.healthData = healthData; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public Double getRiskPercentage() { return riskPercentage; }
    public void setRiskPercentage(Double riskPercentage) { this.riskPercentage = riskPercentage; }

    public String getRiskFactors() { return riskFactors; }
    public void setRiskFactors(String riskFactors) { this.riskFactors = riskFactors; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }

    public String getPdfReportPath() { return pdfReportPath; }
    public void setPdfReportPath(String pdfReportPath) { this.pdfReportPath = pdfReportPath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public enum RiskLevel {
        LOW("Low Risk", 0.0, 30.0),
        MEDIUM("Medium Risk", 30.0, 70.0),
        HIGH("High Risk", 70.0, 100.0);

        private final String description;
        private final double minPercentage;
        private final double maxPercentage;

        RiskLevel(String description, double minPercentage, double maxPercentage) {
            this.description = description;
            this.minPercentage = minPercentage;
            this.maxPercentage = maxPercentage;
        }

        public String getDescription() { return description; }
        public double getMinPercentage() { return minPercentage; }
        public double getMaxPercentage() { return maxPercentage; }

        public static RiskLevel fromPercentage(double percentage) {
            if (percentage < MEDIUM.minPercentage) {
                return LOW;
            } else if (percentage < HIGH.minPercentage) {
                return MEDIUM;
            } else {
                return HIGH;
            }
        }
    }
}
