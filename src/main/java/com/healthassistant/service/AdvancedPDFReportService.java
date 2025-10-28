
package com.healthassistant.service;

import com.healthassistant.model.HealthData;
import com.healthassistant.model.RiskAssessment;
import com.healthassistant.service.RealTimeHealthMonitoringService.HealthTrends;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdvancedPDFReportService {

    private static final String REPORTS_DIR = "reports";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String generateAdvancedHealthReport(RiskAssessment assessment, HealthTrends trends, 
                                             Map<String, Object> additionalData) throws IOException {
        // Create reports directory if it doesn't exist
        File reportsDir = new File(REPORTS_DIR);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }

        HealthData healthData = assessment.getHealthData();
        String fileName = String.format("advanced_health_report_%s_%d.pdf", 
            healthData.getUser().getEmail().replace("@", "_"), 
            System.currentTimeMillis());
        String filePath = REPORTS_DIR + File.separator + fileName;

        // Create PDF document
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        try {
            // Add title page
            addTitlePage(document, healthData, assessment);

            // Add executive summary
            addExecutiveSummary(document, assessment, trends);

            // Add detailed health metrics
            addDetailedHealthMetrics(document, healthData, assessment);

            // Add risk analysis with charts
            addRiskAnalysisWithCharts(document, assessment, trends);

            // Add trend analysis
            addTrendAnalysis(document, trends);

            // Add ML model insights
            addMLModelInsights(document, assessment, additionalData);

            // Add personalized recommendations
            addPersonalizedRecommendations(document, assessment, trends);

            // Add action plan
            addActionPlan(document, assessment);

            // Add medical disclaimer and references
            addMedicalDisclaimer(document);

        } finally {
            document.close();
        }

        return filePath;
    }

    private void addTitlePage(Document document, HealthData healthData, RiskAssessment assessment) throws IOException {
        PdfFont titleFont = PdfFontFactory.createFont();
        
        // Main title
        Paragraph title = new Paragraph("COMPREHENSIVE HEALTH RISK ASSESSMENT REPORT")
            .setFont(titleFont)
            .setFontSize(24)
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(30);
        document.add(title);

        // Subtitle
        Paragraph subtitle = new Paragraph("Intelligent Health Assistant Analysis")
            .setFont(titleFont)
            .setFontSize(16)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(50);
        document.add(subtitle);

        // Patient information box
        Table infoTable = new Table(2).setWidth(400).setHorizontalAlignment(HorizontalAlignment.CENTER);
        
        PdfFont font = PdfFontFactory.createFont();
        
        infoTable.addCell(createStyledCell("Patient Name:", font, true, ColorConstants.LIGHT_GRAY));
        infoTable.addCell(createStyledCell(healthData.getUser().getName(), font, false, ColorConstants.WHITE));
        
        infoTable.addCell(createStyledCell("Email:", font, true, ColorConstants.LIGHT_GRAY));
        infoTable.addCell(createStyledCell(healthData.getUser().getEmail(), font, false, ColorConstants.WHITE));
        
        infoTable.addCell(createStyledCell("Assessment Date:", font, true, ColorConstants.LIGHT_GRAY));
        infoTable.addCell(createStyledCell(assessment.getCreatedAt().format(DATE_FORMATTER), font, false, ColorConstants.WHITE));
        
        infoTable.addCell(createStyledCell("Risk Level:", font, true, ColorConstants.LIGHT_GRAY));
        Cell riskCell = createStyledCell(assessment.getRiskLevel().getDescription(), font, false, ColorConstants.WHITE);
        riskCell.setBold();
        switch (assessment.getRiskLevel()) {
            case LOW:
                riskCell.setFontColor(ColorConstants.GREEN);
                break;
            case MEDIUM:
                riskCell.setFontColor(ColorConstants.ORANGE);
                break;
            case HIGH:
                riskCell.setFontColor(ColorConstants.RED);
                break;
        }
        infoTable.addCell(riskCell);

        document.add(infoTable);

        // Add page break
        document.add(new AreaBreak());
    }

    private void addExecutiveSummary(Document document, RiskAssessment assessment, HealthTrends trends) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph sectionTitle = new Paragraph("EXECUTIVE SUMMARY")
            .setFont(font)
            .setFontSize(18)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(15);
        document.add(sectionTitle);

        // Risk level summary box
        Table summaryTable = new Table(1).setWidth(500);
        
        String summaryText = String.format(
            "Based on comprehensive analysis of your health data, your current risk level is %s (%.1f%%). " +
            "This assessment is based on advanced machine learning algorithms that analyze multiple health factors " +
            "including vital signs, medical history, and lifestyle factors.",
            assessment.getRiskLevel().getDescription(),
            assessment.getRiskPercentage()
        );
        
        Cell summaryCell = new Cell().add(new Paragraph(summaryText).setFont(font).setFontSize(12));
        summaryCell.setPadding(15);
        summaryCell.setBackgroundColor(getRiskLevelColor(assessment.getRiskLevel()));
        summaryTable.addCell(summaryCell);
        
        document.add(summaryTable);

        // Key findings
        Paragraph keyFindingsTitle = new Paragraph("Key Findings:")
            .setFont(font)
            .setFontSize(14)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10);
        document.add(keyFindingsTitle);

        List<String> keyFindings = generateKeyFindings(assessment, trends);
        for (String finding : keyFindings) {
            Paragraph findingPara = new Paragraph("• " + finding)
                .setFont(font)
                .setFontSize(11)
                .setMarginBottom(5);
            document.add(findingPara);
        }
    }

    private void addDetailedHealthMetrics(Document document, HealthData healthData, RiskAssessment assessment) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph sectionTitle = new Paragraph("DETAILED HEALTH METRICS")
            .setFont(font)
            .setFontSize(18)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(15);
        document.add(sectionTitle);

        // Create comprehensive metrics table
        Table metricsTable = new Table(4).setWidth(500);
        
        // Headers
        metricsTable.addCell(createStyledCell("Metric", font, true, ColorConstants.DARK_GRAY));
        metricsTable.addCell(createStyledCell("Value", font, true, ColorConstants.DARK_GRAY));
        metricsTable.addCell(createStyledCell("Normal Range", font, true, ColorConstants.DARK_GRAY));
        metricsTable.addCell(createStyledCell("Status", font, true, ColorConstants.DARK_GRAY));

        // BMI
        Double bmi = healthData.getBMI();
        if (bmi != null) {
            metricsTable.addCell(createStyledCell("BMI", font, false, ColorConstants.WHITE));
            metricsTable.addCell(createStyledCell(String.format("%.1f", bmi), font, false, ColorConstants.WHITE));
            metricsTable.addCell(createStyledCell("18.5 - 24.9", font, false, ColorConstants.WHITE));
            String bmiStatus = getBMIStatus(bmi);
            Cell bmiStatusCell = createStyledCell(bmiStatus, font, false, ColorConstants.WHITE);
            bmiStatusCell.setFontColor(getStatusColor(bmiStatus));
            metricsTable.addCell(bmiStatusCell);
        }

        // Blood Pressure
        if (healthData.getSystolicBP() != null && healthData.getDiastolicBP() != null) {
            metricsTable.addCell(createStyledCell("Blood Pressure", font, false, ColorConstants.WHITE));
            metricsTable.addCell(createStyledCell(healthData.getSystolicBP() + "/" + healthData.getDiastolicBP() + " mmHg", font, false, ColorConstants.WHITE));
            metricsTable.addCell(createStyledCell("< 120/80", font, false, ColorConstants.WHITE));
            String bpStatus = getBPStatus(healthData.getSystolicBP(), healthData.getDiastolicBP());
            Cell bpStatusCell = createStyledCell(bpStatus, font, false, ColorConstants.WHITE);
            bpStatusCell.setFontColor(getStatusColor(bpStatus));
            metricsTable.addCell(bpStatusCell);
        }

        // Heart Rate
        if (healthData.getHeartRate() != null) {
            metricsTable.addCell(createStyledCell("Heart Rate", font, false, ColorConstants.WHITE));
            metricsTable.addCell(createStyledCell(healthData.getHeartRate() + " bpm", font, false, ColorConstants.WHITE));
            metricsTable.addCell(createStyledCell("60 - 100", font, false, ColorConstants.WHITE));
            String hrStatus = getHeartRateStatus(healthData.getHeartRate());
            Cell hrStatusCell = createStyledCell(hrStatus, font, false, ColorConstants.WHITE);
            hrStatusCell.setFontColor(getStatusColor(hrStatus));
            metricsTable.addCell(hrStatusCell);
        }

        // Cholesterol
        if (healthData.getCholesterol() != null) {
            metricsTable.addCell(createStyledCell("Cholesterol", font, false, ColorConstants.WHITE));
            metricsTable.addCell(createStyledCell(healthData.getCholesterol() + " mg/dL", font, false, ColorConstants.WHITE));
            metricsTable.addCell(createStyledCell("< 200", font, false, ColorConstants.WHITE));
            String cholStatus = getCholesterolStatus(healthData.getCholesterol());
            Cell cholStatusCell = createStyledCell(cholStatus, font, false, ColorConstants.WHITE);
            cholStatusCell.setFontColor(getStatusColor(cholStatus));
            metricsTable.addCell(cholStatusCell);
        }

        // Blood Sugar
        if (healthData.getBloodSugar() != null) {
            metricsTable.addCell(createStyledCell("Blood Sugar", font, false, ColorConstants.WHITE));
            metricsTable.addCell(createStyledCell(healthData.getBloodSugar() + " mg/dL", font, false, ColorConstants.WHITE));
            metricsTable.addCell(createStyledCell("< 100", font, false, ColorConstants.WHITE));
            String sugarStatus = getBloodSugarStatus(healthData.getBloodSugar());
            Cell sugarStatusCell = createStyledCell(sugarStatus, font, false, ColorConstants.WHITE);
            sugarStatusCell.setFontColor(getStatusColor(sugarStatus));
            metricsTable.addCell(sugarStatusCell);
        }

        document.add(metricsTable);
    }

    private void addRiskAnalysisWithCharts(Document document, RiskAssessment assessment, HealthTrends trends) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph sectionTitle = new Paragraph("RISK ANALYSIS")
            .setFont(font)
            .setFontSize(18)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(15);
        document.add(sectionTitle);

        // Risk level visualization
        Table riskTable = new Table(1).setWidth(400);
        
        // Create risk level bar
        String riskBar = createRiskLevelBar(assessment.getRiskPercentage());
        Cell riskBarCell = new Cell().add(new Paragraph(riskBar).setFont(font).setFontSize(12));
        riskBarCell.setPadding(10);
        riskBarCell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        riskTable.addCell(riskBarCell);
        
        document.add(riskTable);

        // Risk factors breakdown
        Paragraph riskFactorsTitle = new Paragraph("Risk Factors Breakdown:")
            .setFont(font)
            .setFontSize(14)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10);
        document.add(riskFactorsTitle);

        // Parse risk factors and create detailed breakdown
        String[] factors = assessment.getRiskFactors().split(", ");
        for (String factor : factors) {
            if (!factor.equals("No significant risk factors identified")) {
                Paragraph factorPara = new Paragraph("• " + factor)
                    .setFont(font)
                    .setFontSize(11)
                    .setMarginBottom(5);
                document.add(factorPara);
            }
        }
    }

    private void addTrendAnalysis(Document document, HealthTrends trends) throws IOException {
        if (trends == null || trends.trends == null || trends.trends.isEmpty()) {
            return;
        }

        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph sectionTitle = new Paragraph("HEALTH TRENDS ANALYSIS")
            .setFont(font)
            .setFontSize(18)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(15);
        document.add(sectionTitle);

        Table trendsTable = new Table(3).setWidth(400);
        
        // Headers
        trendsTable.addCell(createStyledCell("Metric", font, true, ColorConstants.DARK_GRAY));
        trendsTable.addCell(createStyledCell("Trend", font, true, ColorConstants.DARK_GRAY));
        trendsTable.addCell(createStyledCell("Description", font, true, ColorConstants.DARK_GRAY));

        // Add trend data
        for (Map.Entry<String, RealTimeHealthMonitoringService.TrendData> entry : trends.trends.entrySet()) {
            String metric = entry.getKey();
            RealTimeHealthMonitoringService.TrendData trendData = entry.getValue();
            
            trendsTable.addCell(createStyledCell(formatMetricName(metric), font, false, ColorConstants.WHITE));
            
            String trendSymbol = trendData.trend > 0 ? "↗" : (trendData.trend < 0 ? "↘" : "→");
            Cell trendCell = createStyledCell(trendSymbol, font, false, ColorConstants.WHITE);
            trendCell.setFontColor(trendData.trend > 0 ? ColorConstants.RED : 
                                 (trendData.trend < 0 ? ColorConstants.GREEN : ColorConstants.BLACK));
            trendsTable.addCell(trendCell);
            
            trendsTable.addCell(createStyledCell(trendData.description, font, false, ColorConstants.WHITE));
        }

        document.add(trendsTable);
    }

    private void addMLModelInsights(Document document, RiskAssessment assessment, Map<String, Object> additionalData) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph sectionTitle = new Paragraph("MACHINE LEARNING INSIGHTS")
            .setFont(font)
            .setFontSize(18)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(15);
        document.add(sectionTitle);

        // Model confidence
        Paragraph confidenceTitle = new Paragraph("Prediction Confidence:")
            .setFont(font)
            .setFontSize(14)
            .setBold()
            .setMarginBottom(10);
        document.add(confidenceTitle);

        double confidence = additionalData != null ? 
            (Double) additionalData.getOrDefault("confidence", 0.85) : 0.85;
        
        String confidenceText = String.format(
            "The machine learning model has %.1f%% confidence in this risk assessment. " +
            "This confidence level is based on the similarity of your health profile to the training data " +
            "and the consistency of your health metrics.",
            confidence * 100
        );
        
        Paragraph confidencePara = new Paragraph(confidenceText)
            .setFont(font)
            .setFontSize(11)
            .setMarginBottom(15);
        document.add(confidencePara);

        // Feature importance
        if (additionalData != null && additionalData.containsKey("featureImportance")) {
            @SuppressWarnings("unchecked")
            Map<String, Double> featureImportance = (Map<String, Double>) additionalData.get("featureImportance");
            
            Paragraph importanceTitle = new Paragraph("Most Important Risk Factors:")
                .setFont(font)
                .setFontSize(14)
                .setBold()
                .setMarginBottom(10);
            document.add(importanceTitle);

            // Sort features by importance
            List<Map.Entry<String, Double>> sortedFeatures = new ArrayList<>(featureImportance.entrySet());
            sortedFeatures.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

            for (int i = 0; i < Math.min(5, sortedFeatures.size()); i++) {
                Map.Entry<String, Double> entry = sortedFeatures.get(i);
                String feature = formatMetricName(entry.getKey());
                double importance = entry.getValue();
                
                Paragraph importancePara = new Paragraph(String.format("%d. %s (Importance: %.2f)", 
                    i + 1, feature, importance))
                    .setFont(font)
                    .setFontSize(11)
                    .setMarginBottom(5);
                document.add(importancePara);
            }
        }
    }

    private void addPersonalizedRecommendations(Document document, RiskAssessment assessment, HealthTrends trends) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph sectionTitle = new Paragraph("PERSONALIZED RECOMMENDATIONS")
            .setFont(font)
            .setFontSize(18)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(15);
        document.add(sectionTitle);

        // Categorize recommendations
        String[] recommendations = assessment.getRecommendations().split("; ");
        
        Map<String, List<String>> categorizedRecs = categorizeRecommendations(recommendations);
        
        for (Map.Entry<String, List<String>> entry : categorizedRecs.entrySet()) {
            String category = entry.getKey();
            List<String> recs = entry.getValue();
            
            Paragraph categoryTitle = new Paragraph(category + ":")
                .setFont(font)
                .setFontSize(14)
                .setBold()
                .setMarginTop(15)
                .setMarginBottom(8);
            document.add(categoryTitle);
            
            for (String rec : recs) {
                Paragraph recPara = new Paragraph("• " + rec)
                    .setFont(font)
                    .setFontSize(11)
                    .setMarginBottom(5);
                document.add(recPara);
            }
        }
    }

    private void addActionPlan(Document document, RiskAssessment assessment) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph sectionTitle = new Paragraph("RECOMMENDED ACTION PLAN")
            .setFont(font)
            .setFontSize(18)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(15);
        document.add(sectionTitle);

        // Create action plan based on risk level
        List<String> actionPlan = generateActionPlan(assessment.getRiskLevel());
        
        Table actionTable = new Table(2).setWidth(500);
        
        // Headers
        actionTable.addCell(createStyledCell("Priority", font, true, ColorConstants.DARK_GRAY));
        actionTable.addCell(createStyledCell("Action", font, true, ColorConstants.DARK_GRAY));

        String[] priorities = {"Immediate", "Short-term", "Long-term"};
        for (int i = 0; i < actionPlan.size() && i < priorities.length; i++) {
            actionTable.addCell(createStyledCell(priorities[i], font, false, ColorConstants.WHITE));
            actionTable.addCell(createStyledCell(actionPlan.get(i), font, false, ColorConstants.WHITE));
        }

        document.add(actionTable);
    }

    private void addMedicalDisclaimer(Document document) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph disclaimerTitle = new Paragraph("MEDICAL DISCLAIMER")
            .setFont(font)
            .setFontSize(16)
            .setBold()
            .setMarginTop(30)
            .setMarginBottom(15);
        document.add(disclaimerTitle);

        String disclaimerText = "This report is generated by an automated intelligent health assistant system " +
            "and is intended for informational purposes only. It should not be used as a substitute for " +
            "professional medical advice, diagnosis, or treatment. Always consult with qualified healthcare " +
            "providers for proper medical evaluation and treatment. The machine learning models used in this " +
            "assessment are based on general population data and may not be applicable to all individuals. " +
            "If you experience any concerning symptoms, please seek immediate medical attention.";

        Paragraph disclaimerPara = new Paragraph(disclaimerText)
            .setFont(font)
            .setFontSize(9)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(20);
        document.add(disclaimerPara);

        // Add generation timestamp
        Paragraph timestamp = new Paragraph("Report generated on: " + 
            java.time.LocalDateTime.now().format(DATE_FORMATTER))
            .setFont(font)
            .setFontSize(8)
            .setTextAlignment(TextAlignment.CENTER);
        document.add(timestamp);
    }

    // Helper methods
    private Cell createStyledCell(String text, PdfFont font, boolean isHeader, com.itextpdf.kernel.colors.Color bgColor) throws IOException {
        Cell cell = new Cell().add(new Paragraph(text).setFont(font).setFontSize(10));
        if (isHeader) {
            cell.setBold();
        }
        cell.setBackgroundColor(bgColor);
        cell.setPadding(8);
        return cell;
    }

    private com.itextpdf.kernel.colors.Color getRiskLevelColor(RiskAssessment.RiskLevel riskLevel) {
        switch (riskLevel) {
            case LOW: return ColorConstants.GREEN;
            case MEDIUM: return ColorConstants.ORANGE;
            case HIGH: return ColorConstants.RED;
            default: return ColorConstants.LIGHT_GRAY;
        }
    }

    private com.itextpdf.kernel.colors.Color getStatusColor(String status) {
        if (status.contains("Normal") || status.contains("Good")) {
            return ColorConstants.GREEN;
        } else if (status.contains("High") || status.contains("Elevated")) {
            return ColorConstants.RED;
        } else if (status.contains("Borderline")) {
            return ColorConstants.ORANGE;
        }
        return ColorConstants.BLACK;
    }

    private String getBMIStatus(double bmi) {
        if (bmi < 18.5) return "Underweight";
        else if (bmi <= 24.9) return "Normal";
        else if (bmi <= 29.9) return "Overweight";
        else return "Obese";
    }

    private String getBPStatus(int systolic, int diastolic) {
        if (systolic < 120 && diastolic < 80) return "Normal";
        else if (systolic < 130 && diastolic < 80) return "Elevated";
        else if (systolic < 140 || diastolic < 90) return "High Blood Pressure Stage 1";
        else return "High Blood Pressure Stage 2";
    }

    private String getHeartRateStatus(int heartRate) {
        if (heartRate < 60) return "Low";
        else if (heartRate <= 100) return "Normal";
        else return "High";
    }

    private String getCholesterolStatus(double cholesterol) {
        if (cholesterol < 200) return "Normal";
        else if (cholesterol < 240) return "Borderline High";
        else return "High";
    }

    private String getBloodSugarStatus(double bloodSugar) {
        if (bloodSugar < 100) return "Normal";
        else if (bloodSugar < 126) return "Pre-diabetes";
        else return "Diabetes";
    }

    private String createRiskLevelBar(double riskPercentage) {
        StringBuilder bar = new StringBuilder();
        int barLength = 50;
        int filledLength = (int) (riskPercentage / 100.0 * barLength);
        
        bar.append("Risk Level: ");
        for (int i = 0; i < barLength; i++) {
            if (i < filledLength) {
                bar.append("█");
            } else {
                bar.append("░");
            }
        }
        bar.append(String.format(" %.1f%%", riskPercentage));
        
        return bar.toString();
    }

    private List<String> generateKeyFindings(RiskAssessment assessment, HealthTrends trends) {
        List<String> findings = new ArrayList<>();
        
        findings.add(String.format("Overall risk level: %s (%.1f%%)", 
            assessment.getRiskLevel().getDescription(), assessment.getRiskPercentage()));
        
        if (assessment.getRiskFactors() != null && !assessment.getRiskFactors().isEmpty()) {
            findings.add("Key risk factors identified: " + assessment.getRiskFactors());
        }
        
        if (trends != null && trends.trends != null) {
            long increasingTrends = trends.trends.values().stream()
                .mapToLong(t -> t.trend > 0 ? 1 : 0)
                .sum();
            if (increasingTrends > 0) {
                findings.add("Some health metrics are trending upward, requiring attention");
            }
        }
        
        return findings;
    }

    private Map<String, List<String>> categorizeRecommendations(String[] recommendations) {
        Map<String, List<String>> categorized = new HashMap<>();
        
        for (String rec : recommendations) {
            String category;
            if (rec.toLowerCase().contains("doctor") || rec.toLowerCase().contains("healthcare") || 
                rec.toLowerCase().contains("consultation")) {
                category = "Medical Consultation";
            } else if (rec.toLowerCase().contains("exercise") || rec.toLowerCase().contains("activity")) {
                category = "Physical Activity";
            } else if (rec.toLowerCase().contains("diet") || rec.toLowerCase().contains("nutrition")) {
                category = "Nutrition";
            } else if (rec.toLowerCase().contains("monitor") || rec.toLowerCase().contains("check")) {
                category = "Monitoring";
            } else {
                category = "General Health";
            }
            
            categorized.computeIfAbsent(category, k -> new ArrayList<>()).add(rec);
        }
        
        return categorized;
    }

    private List<String> generateActionPlan(RiskAssessment.RiskLevel riskLevel) {
        List<String> actions = new ArrayList<>();
        
        switch (riskLevel) {
            case HIGH:
                actions.add("Schedule immediate consultation with healthcare provider");
                actions.add("Implement lifestyle changes within 2 weeks");
                actions.add("Develop long-term health management plan");
                break;
            case MEDIUM:
                actions.add("Schedule regular health check-ups");
                actions.add("Start lifestyle modifications within 1 month");
                actions.add("Monitor progress and adjust plan as needed");
                break;
            case LOW:
                actions.add("Maintain current healthy habits");
                actions.add("Continue regular health screenings");
                actions.add("Consider preventive health measures");
                break;
        }
        
        return actions;
    }

    private String formatMetricName(String metric) {
        return metric.replace("_", " ").replace("bp", "Blood Pressure")
                    .replace("hr", "Heart Rate").replace("chol", "Cholesterol")
                    .replace("sugar", "Blood Sugar");
    }
}
