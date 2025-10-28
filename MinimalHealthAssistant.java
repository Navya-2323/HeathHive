package com.healthassistant;

import java.util.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.net.*;

public class MinimalHealthAssistant {
    
    private static final int PORT = 8080;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Intelligent Health Assistant");
        System.out.println("   Minimal Web Server Version");
        System.out.println("========================================");
        System.out.println();
        
        // Start web server
        startWebServer();
    }
    
    private static void startWebServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Health Assistant server started on port " + PORT);
            System.out.println("Access the application at:");
            System.out.println("  - Web Interface: http://localhost:" + PORT);
            System.out.println("  - API Health Check: http://localhost:" + PORT + "/health");
            System.out.println();
            System.out.println("Press Ctrl+C to stop the server");
            System.out.println();
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(() -> handleRequest(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
    
    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            String requestLine = in.readLine();
            if (requestLine == null) return;
            
            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String path = parts[1];
            
            if ("GET".equals(method)) {
                handleGetRequest(path, out);
            } else if ("POST".equals(method)) {
                handlePostRequest(path, in, out);
            }
            
        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
        }
    }
    
    private static void handleGetRequest(String path, PrintWriter out) {
        if ("/health".equals(path)) {
            sendJsonResponse(out, "{\"status\":\"running\",\"message\":\"Health Assistant API is running!\"}");
        } else if ("/".equals(path)) {
            sendHtmlResponse(out, getWebInterface());
        } else {
            sendJsonResponse(out, "{\"error\":\"Not found\"}");
        }
    }
    
    private static void handlePostRequest(String path, BufferedReader in, PrintWriter out) {
        if ("/api/health/assess".equals(path)) {
            try {
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    requestBody.append(line);
                }
                
                // Parse JSON and process health assessment
                HealthDataRequest request = parseHealthDataRequest(requestBody.toString());
                RiskAssessment assessment = processHealthAssessment(request);
                
                sendJsonResponse(out, assessment.toJson());
            } catch (Exception e) {
                sendJsonResponse(out, "{\"error\":\"Processing failed: " + e.getMessage() + "\"}");
            }
        } else {
            sendJsonResponse(out, "{\"error\":\"Not found\"}");
        }
    }
    
    private static void sendJsonResponse(PrintWriter out, String json) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println("Access-Control-Allow-Origin: *");
        out.println("Content-Length: " + json.length());
        out.println();
        out.println(json);
    }
    
    private static void sendHtmlResponse(PrintWriter out, String html) {
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html");
        out.println("Content-Length: " + html.length());
        out.println();
        out.println(html);
    }
    
    private static HealthDataRequest parseHealthDataRequest(String json) {
        // Simple JSON parsing (in a real app, use a proper JSON library)
        HealthDataRequest request = new HealthDataRequest();
        
        // Extract values using simple string operations
        request.name = extractValue(json, "name");
        request.email = extractValue(json, "email");
        request.age = Integer.parseInt(extractValue(json, "age"));
        request.gender = extractValue(json, "gender");
        request.height = Double.parseDouble(extractValue(json, "height"));
        request.weight = Double.parseDouble(extractValue(json, "weight"));
        request.systolicBP = Integer.parseInt(extractValue(json, "systolicBP"));
        request.diastolicBP = Integer.parseInt(extractValue(json, "diastolicBP"));
        request.heartRate = Integer.parseInt(extractValue(json, "heartRate"));
        request.cholesterol = Double.parseDouble(extractValue(json, "cholesterol"));
        request.bloodSugar = Double.parseDouble(extractValue(json, "bloodSugar"));
        request.hasDiabetes = Boolean.parseBoolean(extractValue(json, "hasDiabetes"));
        request.hasHypertension = Boolean.parseBoolean(extractValue(json, "hasHypertension"));
        request.hasHeartDisease = Boolean.parseBoolean(extractValue(json, "hasHeartDisease"));
        request.smokes = Boolean.parseBoolean(extractValue(json, "smokes"));
        request.exercisesRegularly = Boolean.parseBoolean(extractValue(json, "exercisesRegularly"));
        
        return request;
    }
    
    private static String extractValue(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"?([^\",}]+)\"?";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1).replace("\"", "");
        }
        return "0"; // Default value
    }
    
    private static RiskAssessment processHealthAssessment(HealthDataRequest request) {
        // Calculate risk using ML algorithm
        double riskScore = calculateRiskScore(request);
        RiskLevel riskLevel = determineRiskLevel(riskScore);
        String riskFactors = identifyRiskFactors(request, riskScore);
        String recommendations = generateRecommendations(request, riskLevel);
        
        return new RiskAssessment(request, riskLevel, riskScore, riskFactors, recommendations);
    }
    
    private static double calculateRiskScore(HealthDataRequest request) {
        double riskScore = 0.0;
        
        // Age factor
        riskScore += request.age * 0.5;
        
        // BMI factor
        double bmi = request.weight / Math.pow(request.height / 100.0, 2);
        if (bmi < 18.5) {
            riskScore += 5.0;
        } else if (bmi > 25 && bmi <= 30) {
            riskScore += 10.0;
        } else if (bmi > 30) {
            riskScore += 20.0;
        }
        
        // Blood pressure factors
        if (request.systolicBP > 140 || request.diastolicBP > 90) {
            riskScore += 15.0;
        } else if (request.systolicBP > 120 || request.diastolicBP > 80) {
            riskScore += 8.0;
        }
        
        // Heart rate factor
        if (request.heartRate < 60 || request.heartRate > 100) {
            riskScore += 5.0;
        }
        
        // Cholesterol factor
        if (request.cholesterol > 200) {
            riskScore += 12.0;
        }
        
        // Blood sugar factor
        if (request.bloodSugar > 126) {
            riskScore += 15.0;
        } else if (request.bloodSugar > 100) {
            riskScore += 8.0;
        }
        
        // Lifestyle factors
        if (request.smokes) {
            riskScore += 20.0;
        }
        
        if (request.hasDiabetes) {
            riskScore += 15.0;
        }
        
        if (request.hasHypertension) {
            riskScore += 12.0;
        }
        
        if (request.hasHeartDisease) {
            riskScore += 25.0;
        }
        
        if (request.exercisesRegularly) {
            riskScore -= 8.0;
        }
        
        if ("MALE".equals(request.gender)) {
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
    
    private static String identifyRiskFactors(HealthDataRequest request, double riskScore) {
        List<String> factors = new ArrayList<>();
        
        if (request.age > 65) {
            factors.add("Advanced age");
        }
        
        double bmi = request.weight / Math.pow(request.height / 100.0, 2);
        if (bmi < 18.5) {
            factors.add("Underweight (BMI: " + String.format("%.1f", bmi) + ")");
        } else if (bmi > 25) {
            factors.add("High BMI (BMI: " + String.format("%.1f", bmi) + ")");
        }
        
        if (request.systolicBP > 140 || request.diastolicBP > 90) {
            factors.add("Hypertension (" + request.systolicBP + "/" + request.diastolicBP + " mmHg)");
        }
        
        if (request.cholesterol > 200) {
            factors.add("High cholesterol (" + request.cholesterol + " mg/dL)");
        }
        
        if (request.bloodSugar > 126) {
            factors.add("High blood sugar (" + request.bloodSugar + " mg/dL)");
        }
        
        if (request.smokes) {
            factors.add("Smoking");
        }
        
        if (request.hasDiabetes) {
            factors.add("Diabetes");
        }
        
        if (request.hasHypertension) {
            factors.add("Hypertension");
        }
        
        if (request.hasHeartDisease) {
            factors.add("Heart disease");
        }
        
        if (factors.isEmpty()) {
            factors.add("No significant risk factors identified");
        }
        
        return String.join(", ", factors);
    }
    
    private static String generateRecommendations(HealthDataRequest request, RiskLevel riskLevel) {
        List<String> recommendations = new ArrayList<>();
        
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
        
        double bmi = request.weight / Math.pow(request.height / 100.0, 2);
        if (bmi > 25) {
            recommendations.add("Consider weight management program");
            recommendations.add("Increase physical activity");
        }
        
        if (request.systolicBP > 120) {
            recommendations.add("Monitor blood pressure regularly");
            recommendations.add("Consider dietary changes to reduce sodium intake");
        }
        
        if (request.cholesterol > 200) {
            recommendations.add("Consider dietary changes to reduce cholesterol");
            recommendations.add("Discuss cholesterol management with healthcare provider");
        }
        
        if (request.smokes) {
            recommendations.add("Consider smoking cessation programs");
            recommendations.add("Seek support for quitting smoking");
        }
        
        if (!request.exercisesRegularly) {
            recommendations.add("Start regular exercise routine");
            recommendations.add("Aim for at least 150 minutes of moderate exercise per week");
        }
        
        return String.join("; ", recommendations);
    }
    
    private static String getWebInterface() {
        return "<!DOCTYPE html>" +
               "<html><head><title>Health Assistant</title></head>" +
               "<body><h1>🏥 Intelligent Health Assistant</h1>" +
               "<p>This is a minimal web server version of the Health Assistant.</p>" +
               "<p>For the full Spring Boot version with PDF reports and database integration, install Maven and run: <code>mvn spring-boot:run</code></p>" +
               "<p>API Health Check: <a href='/health'>/health</a></p>" +
               "</body></html>";
    }
    
    // Data classes
    static class HealthDataRequest {
        String name, email, gender;
        int age, systolicBP, diastolicBP, heartRate;
        double height, weight, cholesterol, bloodSugar;
        boolean hasDiabetes, hasHypertension, hasHeartDisease, smokes, exercisesRegularly;
    }
    
    static class RiskAssessment {
        HealthDataRequest request;
        RiskLevel riskLevel;
        double riskScore;
        String riskFactors, recommendations;
        
        RiskAssessment(HealthDataRequest request, RiskLevel riskLevel, double riskScore, String riskFactors, String recommendations) {
            this.request = request;
            this.riskLevel = riskLevel;
            this.riskScore = riskScore;
            this.riskFactors = riskFactors;
            this.recommendations = recommendations;
        }
        
        String toJson() {
            return String.format(
                "{\"userName\":\"%s\",\"userEmail\":\"%s\",\"riskLevel\":\"%s\",\"riskPercentage\":%.1f,\"riskFactors\":\"%s\",\"recommendations\":\"%s\",\"message\":\"Health assessment completed successfully\"}",
                request.name, request.email, riskLevel, riskScore, riskFactors, recommendations
            );
        }
    }
    
    enum RiskLevel {
        LOW("Low Risk"), MEDIUM("Medium Risk"), HIGH("High Risk");
        
        private final String description;
        RiskLevel(String description) { this.description = description; }
        @Override
        public String toString() { return description; }
    }
}
