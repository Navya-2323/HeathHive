package com.healthassistant.service;

import com.healthassistant.model.HealthData;
import com.healthassistant.model.RiskAssessment;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PDFReportService {

    private static final String REPORTS_DIR = "reports";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String generateHealthReport(RiskAssessment assessment) throws IOException {
        // Create reports directory if it doesn't exist
        File reportsDir = new File(REPORTS_DIR);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }

        HealthData healthData = assessment.getHealthData();
        String fileName = String.format("health_report_%s_%d.pdf", 
            healthData.getUser().getEmail().replace("@", "_"), 
            System.currentTimeMillis());
        String filePath = REPORTS_DIR + File.separator + fileName;

        // Create PDF document
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        try {
            // Add title
            PdfFont titleFont = PdfFontFactory.createFont();
            Paragraph title = new Paragraph("HEALTH RISK ASSESSMENT REPORT")
                .setFont(titleFont)
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
            document.add(title);

            // Add report date
            Paragraph reportDate = new Paragraph("Report Generated: " + 
                assessment.getCreatedAt().format(DATE_FORMATTER))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30);
            document.add(reportDate);

            // Add patient information
            addPatientInfo(document, healthData);

            // Add health metrics
            addHealthMetrics(document, healthData);

            // Add risk assessment
            addRiskAssessment(document, assessment);

            // Add recommendations
            addRecommendations(document, assessment);

            // Add disclaimer
            addDisclaimer(document);

        } finally {
            document.close();
        }

        return filePath;
    }

    private void addPatientInfo(Document document, HealthData healthData) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph sectionTitle = new Paragraph("PATIENT INFORMATION")
            .setFont(font)
            .setFontSize(14)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10);
        document.add(sectionTitle);

        Table infoTable = new Table(2).setWidth(500);
        
        infoTable.addCell(createCell("Name:", font, true));
        infoTable.addCell(createCell(healthData.getUser().getName(), font, false));
        
        infoTable.addCell(createCell("Email:", font, true));
        infoTable.addCell(createCell(healthData.getUser().getEmail(), font, false));
        
        infoTable.addCell(createCell("Age:", font, true));
        infoTable.addCell(createCell(healthData.getUser().getAge().toString(), font, false));
        
        infoTable.addCell(createCell("Gender:", font, true));
        infoTable.addCell(createCell(healthData.getUser().getGender().toString(), font, false));
        
        infoTable.addCell(createCell("Assessment Date:", font, true));
        infoTable.addCell(createCell(healthData.getSubmittedAt().format(DATE_FORMATTER), font, false));

        document.add(infoTable);
    }

    private void addHealthMetrics(Document document, HealthData healthData) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph sectionTitle = new Paragraph("HEALTH METRICS")
            .setFont(font)
            .setFontSize(14)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10);
        document.add(sectionTitle);

        Table metricsTable = new Table(2).setWidth(500);
        
        if (healthData.getHeight() != null && healthData.getWeight() != null) {
            metricsTable.addCell(createCell("Height:", font, true));
            metricsTable.addCell(createCell(healthData.getHeight() + " cm", font, false));
            
            metricsTable.addCell(createCell("Weight:", font, true));
            metricsTable.addCell(createCell(healthData.getWeight() + " kg", font, false));
            
            metricsTable.addCell(createCell("BMI:", font, true));
            metricsTable.addCell(createCell(String.format("%.1f", healthData.getBMI()), font, false));
        }
        
        if (healthData.getSystolicBP() != null && healthData.getDiastolicBP() != null) {
            metricsTable.addCell(createCell("Blood Pressure:", font, true));
            metricsTable.addCell(createCell(healthData.getSystolicBP() + "/" + healthData.getDiastolicBP() + " mmHg", font, false));
        }
        
        if (healthData.getHeartRate() != null) {
            metricsTable.addCell(createCell("Heart Rate:", font, true));
            metricsTable.addCell(createCell(healthData.getHeartRate() + " bpm", font, false));
        }
        
        if (healthData.getCholesterol() != null) {
            metricsTable.addCell(createCell("Cholesterol:", font, true));
            metricsTable.addCell(createCell(healthData.getCholesterol() + " mg/dL", font, false));
        }
        
        if (healthData.getBloodSugar() != null) {
            metricsTable.addCell(createCell("Blood Sugar:", font, true));
            metricsTable.addCell(createCell(healthData.getBloodSugar() + " mg/dL", font, false));
        }

        document.add(metricsTable);
    }

    private void addRiskAssessment(Document document, RiskAssessment assessment) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph sectionTitle = new Paragraph("RISK ASSESSMENT")
            .setFont(font)
            .setFontSize(14)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10);
        document.add(sectionTitle);

        // Risk level with color coding
        com.itextpdf.layout.element.Paragraph riskLevelPara = new Paragraph("Risk Level: " + assessment.getRiskLevel().getDescription())
            .setFont(font)
            .setFontSize(16)
            .setBold();
        
        // Color code based on risk level
        switch (assessment.getRiskLevel()) {
            case LOW:
                riskLevelPara.setFontColor(ColorConstants.GREEN);
                break;
            case MEDIUM:
                riskLevelPara.setFontColor(ColorConstants.ORANGE);
                break;
            case HIGH:
                riskLevelPara.setFontColor(ColorConstants.RED);
                break;
        }
        
        document.add(riskLevelPara);

        Paragraph riskPercentage = new Paragraph("Risk Percentage: " + 
            String.format("%.1f", assessment.getRiskPercentage()) + "%")
            .setFont(font)
            .setFontSize(12)
            .setMarginBottom(10);
        document.add(riskPercentage);

        if (assessment.getRiskFactors() != null && !assessment.getRiskFactors().isEmpty()) {
            Paragraph riskFactorsTitle = new Paragraph("Identified Risk Factors:")
                .setFont(font)
                .setFontSize(12)
                .setBold()
                .setMarginBottom(5);
            document.add(riskFactorsTitle);

            Paragraph riskFactors = new Paragraph(assessment.getRiskFactors())
                .setFont(font)
                .setFontSize(10)
                .setMarginBottom(15);
            document.add(riskFactors);
        }
    }

    private void addRecommendations(Document document, RiskAssessment assessment) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph sectionTitle = new Paragraph("RECOMMENDATIONS")
            .setFont(font)
            .setFontSize(14)
            .setBold()
            .setMarginTop(20)
            .setMarginBottom(10);
        document.add(sectionTitle);

        if (assessment.getRecommendations() != null && !assessment.getRecommendations().isEmpty()) {
            Paragraph recommendations = new Paragraph(assessment.getRecommendations())
                .setFont(font)
                .setFontSize(10)
                .setMarginBottom(15);
            document.add(recommendations);
        }
    }

    private void addDisclaimer(Document document) throws IOException {
        PdfFont font = PdfFontFactory.createFont();
        
        Paragraph disclaimer = new Paragraph("DISCLAIMER: This report is generated by an automated system and should not replace professional medical advice. Please consult with a qualified healthcare provider for proper medical evaluation and treatment.")
            .setFont(font)
            .setFontSize(8)
            .setItalic()
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginTop(30)
            .setMarginBottom(20);
        document.add(disclaimer);
    }

    private Cell createCell(String text, PdfFont font, boolean isHeader) throws IOException {
        Cell cell = new Cell().add(new Paragraph(text).setFont(font).setFontSize(10));
        if (isHeader) {
            cell.setBold();
            cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        }
        return cell;
    }
}
