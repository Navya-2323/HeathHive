package com.healthassistant.service;

import com.healthassistant.ml.AdvancedMLService;
import com.healthassistant.model.HealthData;
import com.healthassistant.model.User;
import java.util.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Real-time Health Monitoring Service
 * Features: Continuous monitoring, anomaly detection, alerts, and trend analysis
 */
@Service
public class RealTimeHealthMonitoringService {
    
    private final AdvancedMLService mlService;
    private final Map<String, List<HealthReading>> userReadings;
    private final Map<String, HealthAlert> activeAlerts;
    private final ScheduledExecutorService scheduler;
    private final ExecutorService alertExecutor;
    
    // Monitoring thresholds
    private static final double ANOMALY_THRESHOLD = 2.0; // Standard deviations
    private static final Duration MONITORING_INTERVAL = Duration.ofMinutes(5);
    private static final int MAX_READINGS_PER_USER = 1000;
    
    public RealTimeHealthMonitoringService() {
        this.mlService = new AdvancedMLService();
        this.userReadings = new ConcurrentHashMap<>();
        this.activeAlerts = new ConcurrentHashMap<>();
        this.scheduler = Executors.newScheduledThreadPool(4);
        this.alertExecutor = Executors.newFixedThreadPool(10);
        
        // Start monitoring tasks
        startMonitoringTasks();
    }
    
    /**
     * Start all monitoring tasks
     */
    private void startMonitoringTasks() {
        // Anomaly detection task
        scheduler.scheduleAtFixedRate(this::detectAnomalies, 0, 5, TimeUnit.MINUTES);
        
        // Alert processing task
        scheduler.scheduleAtFixedRate(this::processAlerts, 0, 1, TimeUnit.MINUTES);
        
        // Model retraining task
        scheduler.scheduleAtFixedRate(this::retrainModel, 0, 1, TimeUnit.HOURS);
        
        // Data cleanup task
        scheduler.scheduleAtFixedRate(this::cleanupOldData, 0, 1, TimeUnit.HOURS);
        
        System.out.println("Real-time health monitoring started");
    }
    
    /**
     * Add new health reading for monitoring
     */
    public void addHealthReading(String userEmail, HealthReading reading) {
        userReadings.computeIfAbsent(userEmail, k -> new ArrayList<>()).add(reading);
        
        // Keep only recent readings
        List<HealthReading> readings = userReadings.get(userEmail);
        if (readings.size() > MAX_READINGS_PER_USER) {
            readings.remove(0); // Remove oldest
        }
        
        // Immediate risk assessment
        assessImmediateRisk(userEmail, reading);
        
        System.out.println("Health reading added for " + userEmail + " at " + reading.timestamp);
    }
    
    /**
     * Assess immediate risk for a new reading
     */
    private void assessImmediateRisk(String userEmail, HealthReading reading) {
        Map<String, Double> features = extractFeatures(reading);
        AdvancedMLService.MLPrediction prediction = mlService.predictRisk(features);
        
        // Check for critical values
        if (isCriticalValue(reading)) {
            createAlert(userEmail, AlertType.CRITICAL, 
                "Critical health values detected", reading, prediction);
        } else if (prediction.riskLevel == AdvancedMLService.RiskLevel.HIGH) {
            createAlert(userEmail, AlertType.HIGH_RISK, 
                "High risk assessment", reading, prediction);
        } else if (isAnomalous(userEmail, reading)) {
            createAlert(userEmail, AlertType.ANOMALY, 
                "Unusual health pattern detected", reading, prediction);
        }
    }
    
    /**
     * Detect anomalies across all users
     */
    private void detectAnomalies() {
        for (Map.Entry<String, List<HealthReading>> entry : userReadings.entrySet()) {
            String userEmail = entry.getKey();
            List<HealthReading> readings = entry.getValue();
            
            if (readings.size() < 5) continue; // Need minimum readings
            
            // Check for anomalies in recent readings
            List<HealthReading> recentReadings = readings.stream()
                .filter(r -> Duration.between(r.timestamp, LocalDateTime.now()).toHours() < 24)
                .collect(Collectors.toList());
            
            for (HealthReading reading : recentReadings) {
                if (isAnomalous(userEmail, reading)) {
                    Map<String, Double> features = extractFeatures(reading);
                    AdvancedMLService.MLPrediction prediction = mlService.predictRisk(features);
                    
                    createAlert(userEmail, AlertType.ANOMALY, 
                        "Anomalous reading detected", reading, prediction);
                }
            }
        }
    }
    
    /**
     * Check if a reading is anomalous
     */
    private boolean isAnomalous(String userEmail, HealthReading reading) {
        List<HealthReading> historicalReadings = userReadings.get(userEmail);
        if (historicalReadings.size() < 10) return false;
        
        // Calculate statistics for each metric
        Map<String, Double> means = calculateMeans(historicalReadings);
        Map<String, Double> stdDevs = calculateStdDevs(historicalReadings, means);
        
        // Check each metric for anomalies
        return isAnomalousMetric(reading.systolicBP, means.get("systolicBP"), stdDevs.get("systolicBP")) ||
               isAnomalousMetric(reading.diastolicBP, means.get("diastolicBP"), stdDevs.get("diastolicBP")) ||
               isAnomalousMetric(reading.heartRate, means.get("heartRate"), stdDevs.get("heartRate")) ||
               isAnomalousMetric(reading.cholesterol, means.get("cholesterol"), stdDevs.get("cholesterol")) ||
               isAnomalousMetric(reading.bloodSugar, means.get("bloodSugar"), stdDevs.get("bloodSugar"));
    }
    
    /**
     * Check if a single metric is anomalous
     */
    private boolean isAnomalousMetric(double value, double mean, double stdDev) {
        if (stdDev == 0) return false;
        double zScore = Math.abs((value - mean) / stdDev);
        return zScore > ANOMALY_THRESHOLD;
    }
    
    /**
     * Check if reading contains critical values
     */
    private boolean isCriticalValue(HealthReading reading) {
        return reading.systolicBP > 180 || reading.systolicBP < 70 ||
               reading.diastolicBP > 110 || reading.diastolicBP < 40 ||
               reading.heartRate > 150 || reading.heartRate < 30 ||
               reading.cholesterol > 300 ||
               reading.bloodSugar > 200 || reading.bloodSugar < 50;
    }
    
    /**
     * Create a health alert
     */
    private void createAlert(String userEmail, AlertType type, String message, 
                           HealthReading reading, AdvancedMLService.MLPrediction prediction) {
        HealthAlert alert = new HealthAlert(
            userEmail, type, message, reading, prediction, LocalDateTime.now()
        );
        
        activeAlerts.put(userEmail + "_" + alert.id, alert);
        
        // Process alert asynchronously
        alertExecutor.submit(() -> processAlert(alert));
        
        System.out.println("Alert created for " + userEmail + ": " + message);
    }
    
    /**
     * Process a health alert
     */
    private void processAlert(HealthAlert alert) {
        // Send notification (email, SMS, push notification)
        sendNotification(alert);
        
        // Log alert
        logAlert(alert);
        
        // Update user's health status
        updateUserHealthStatus(alert);
    }
    
    /**
     * Process all active alerts
     */
    private void processAlerts() {
        List<HealthAlert> alertsToProcess = new ArrayList<>(activeAlerts.values());
        
        for (HealthAlert alert : alertsToProcess) {
            // Remove old alerts (older than 24 hours)
            if (Duration.between(alert.createdAt, LocalDateTime.now()).toHours() > 24) {
                activeAlerts.remove(alert.userEmail + "_" + alert.id);
            }
        }
    }
    
    /**
     * Retrain the ML model with new data
     */
    private void retrainModel() {
        List<AdvancedMLService.TrainingExample> newTrainingData = new ArrayList<>();
        
        // Collect recent assessments for retraining
        for (Map.Entry<String, List<HealthReading>> entry : userReadings.entrySet()) {
            String userEmail = entry.getKey();
            List<HealthReading> readings = entry.getValue();
            
            if (readings.size() < 5) continue;
            
            // Use recent readings to create training examples
            HealthReading latestReading = readings.get(readings.size() - 1);
            Map<String, Double> features = extractFeatures(latestReading);
            
            // Determine actual risk level based on medical outcomes
            AdvancedMLService.RiskLevel actualRiskLevel = determineActualRiskLevel(userEmail, latestReading);
            
            newTrainingData.add(new AdvancedMLService.TrainingExample(features, actualRiskLevel));
        }
        
        if (!newTrainingData.isEmpty()) {
            mlService.trainModel(newTrainingData);
            System.out.println("Model retrained with " + newTrainingData.size() + " new examples");
        }
    }
    
    /**
     * Determine actual risk level based on outcomes
     */
    private AdvancedMLService.RiskLevel determineActualRiskLevel(String userEmail, HealthReading reading) {
        // This would typically be based on actual medical outcomes
        // For now, use a simplified approach based on critical values
        
        if (isCriticalValue(reading)) {
            return AdvancedMLService.RiskLevel.HIGH;
        } else if (reading.systolicBP > 140 || reading.diastolicBP > 90 || 
                  reading.cholesterol > 200 || reading.bloodSugar > 126) {
            return AdvancedMLService.RiskLevel.MEDIUM;
        } else {
            return AdvancedMLService.RiskLevel.LOW;
        }
    }
    
    /**
     * Clean up old data
     */
    private void cleanupOldData() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        
        for (List<HealthReading> readings : userReadings.values()) {
            readings.removeIf(reading -> reading.timestamp.isBefore(cutoff));
        }
        
        System.out.println("Old data cleaned up");
    }
    
    /**
     * Extract features from health reading
     */
    private Map<String, Double> extractFeatures(HealthReading reading) {
        Map<String, Double> features = new HashMap<>();
        
        // Calculate BMI if height and weight are available
        if (reading.height > 0 && reading.weight > 0) {
            double bmi = reading.weight / Math.pow(reading.height / 100.0, 2);
            features.put("bmi", bmi);
        }
        
        features.put("systolic_bp", (double) reading.systolicBP);
        features.put("diastolic_bp", (double) reading.diastolicBP);
        features.put("heart_rate", (double) reading.heartRate);
        features.put("cholesterol", reading.cholesterol);
        features.put("blood_sugar", reading.bloodSugar);
        features.put("smoking", reading.smokes ? 1.0 : 0.0);
        features.put("diabetes", reading.hasDiabetes ? 1.0 : 0.0);
        features.put("hypertension", reading.hasHypertension ? 1.0 : 0.0);
        features.put("heart_disease", reading.hasHeartDisease ? 1.0 : 0.0);
        features.put("exercise", reading.exercisesRegularly ? 1.0 : 0.0);
        features.put("gender_male", reading.gender.equals("MALE") ? 1.0 : 0.0);
        
        return features;
    }
    
    /**
     * Calculate means for historical readings
     */
    private Map<String, Double> calculateMeans(List<HealthReading> readings) {
        Map<String, Double> means = new HashMap<>();
        
        means.put("systolicBP", readings.stream().mapToDouble(r -> r.systolicBP).average().orElse(0.0));
        means.put("diastolicBP", readings.stream().mapToDouble(r -> r.diastolicBP).average().orElse(0.0));
        means.put("heartRate", readings.stream().mapToDouble(r -> r.heartRate).average().orElse(0.0));
        means.put("cholesterol", readings.stream().mapToDouble(r -> r.cholesterol).average().orElse(0.0));
        means.put("bloodSugar", readings.stream().mapToDouble(r -> r.bloodSugar).average().orElse(0.0));
        
        return means;
    }
    
    /**
     * Calculate standard deviations for historical readings
     */
    private Map<String, Double> calculateStdDevs(List<HealthReading> readings, Map<String, Double> means) {
        Map<String, Double> stdDevs = new HashMap<>();
        
        for (Map.Entry<String, Double> entry : means.entrySet()) {
            String metric = entry.getKey();
            double mean = entry.getValue();
            
            double variance = readings.stream()
                .mapToDouble(r -> getMetricValue(r, metric))
                .map(v -> Math.pow(v - mean, 2))
                .average()
                .orElse(0.0);
            
            stdDevs.put(metric, Math.sqrt(variance));
        }
        
        return stdDevs;
    }
    
    /**
     * Get metric value from reading
     */
    private double getMetricValue(HealthReading reading, String metric) {
        switch (metric) {
            case "systolicBP": return reading.systolicBP;
            case "diastolicBP": return reading.diastolicBP;
            case "heartRate": return reading.heartRate;
            case "cholesterol": return reading.cholesterol;
            case "bloodSugar": return reading.bloodSugar;
            default: return 0.0;
        }
    }
    
    /**
     * Send notification for alert
     */
    private void sendNotification(HealthAlert alert) {
        // Implementation would send email, SMS, or push notification
        System.out.println("NOTIFICATION: " + alert.message + " for " + alert.userEmail);
    }
    
    /**
     * Log alert
     */
    private void logAlert(HealthAlert alert) {
        // Implementation would log to database or file
        System.out.println("ALERT LOGGED: " + alert.toString());
    }
    
    /**
     * Update user health status
     */
    private void updateUserHealthStatus(HealthAlert alert) {
        // Implementation would update user's health status in database
        System.out.println("HEALTH STATUS UPDATED for " + alert.userEmail);
    }
    
    /**
     * Get health trends for a user
     */
    public HealthTrends getHealthTrends(String userEmail) {
        List<HealthReading> readings = userReadings.getOrDefault(userEmail, new ArrayList<>());
        
        if (readings.isEmpty()) {
            return new HealthTrends(userEmail, "No data available", new HashMap<>());
        }
        
        // Calculate trends for each metric
        Map<String, TrendData> trends = new HashMap<>();
        
        trends.put("systolicBP", calculateTrend(readings, r -> (double) r.systolicBP));
        trends.put("diastolicBP", calculateTrend(readings, r -> (double) r.diastolicBP));
        trends.put("heartRate", calculateTrend(readings, r -> (double) r.heartRate));
        trends.put("cholesterol", calculateTrend(readings, r -> r.cholesterol));
        trends.put("bloodSugar", calculateTrend(readings, r -> r.bloodSugar));
        
        return new HealthTrends(userEmail, "Trends calculated", trends);
    }
    
    /**
     * Calculate trend for a specific metric
     */
    private TrendData calculateTrend(List<HealthReading> readings, java.util.function.Function<HealthReading, Double> extractor) {
        if (readings.size() < 2) {
            return new TrendData(0.0, "Insufficient data");
        }
        
        // Simple linear trend calculation
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
        int n = readings.size();
        
        for (int i = 0; i < n; i++) {
            double x = i; // Time index
            double y = extractor.apply(readings.get(i));
            
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        double trend = slope > 0.1 ? 1.0 : (slope < -0.1 ? -1.0 : 0.0); // 1 = increasing, -1 = decreasing, 0 = stable
        
        String trendDescription = trend > 0 ? "Increasing" : (trend < 0 ? "Decreasing" : "Stable");
        
        return new TrendData(trend, trendDescription);
    }
    
    /**
     * Get active alerts for a user
     */
    public List<HealthAlert> getActiveAlerts(String userEmail) {
        return activeAlerts.values().stream()
            .filter(alert -> alert.userEmail.equals(userEmail))
            .collect(Collectors.toList());
    }
    
    /**
     * Get model statistics
     */
    public AdvancedMLService.ModelStatistics getModelStatistics() {
        return mlService.getModelStatistics();
    }
    
    /**
     * Shutdown the monitoring service
     */
    public void shutdown() {
        scheduler.shutdown();
        alertExecutor.shutdown();
        System.out.println("Real-time health monitoring stopped");
    }
    
    // Data classes
    public static class HealthReading {
        public final LocalDateTime timestamp;
        public final int systolicBP;
        public final int diastolicBP;
        public final int heartRate;
        public final double cholesterol;
        public final double bloodSugar;
        public final double height;
        public final double weight;
        public final boolean smokes;
        public final boolean hasDiabetes;
        public final boolean hasHypertension;
        public final boolean hasHeartDisease;
        public final boolean exercisesRegularly;
        public final String gender;
        
        public HealthReading(int systolicBP, int diastolicBP, int heartRate, double cholesterol, 
                           double bloodSugar, double height, double weight, boolean smokes,
                           boolean hasDiabetes, boolean hasHypertension, boolean hasHeartDisease,
                           boolean exercisesRegularly, String gender) {
            this.timestamp = LocalDateTime.now();
            this.systolicBP = systolicBP;
            this.diastolicBP = diastolicBP;
            this.heartRate = heartRate;
            this.cholesterol = cholesterol;
            this.bloodSugar = bloodSugar;
            this.height = height;
            this.weight = weight;
            this.smokes = smokes;
            this.hasDiabetes = hasDiabetes;
            this.hasHypertension = hasHypertension;
            this.hasHeartDisease = hasHeartDisease;
            this.exercisesRegularly = exercisesRegularly;
            this.gender = gender;
        }
    }
    
    public static class HealthAlert {
        public final String id;
        public final String userEmail;
        public final AlertType type;
        public final String message;
        public final HealthReading reading;
        public final AdvancedMLService.MLPrediction prediction;
        public final LocalDateTime createdAt;
        
        public HealthAlert(String userEmail, AlertType type, String message, 
                          HealthReading reading, AdvancedMLService.MLPrediction prediction, 
                          LocalDateTime createdAt) {
            this.id = UUID.randomUUID().toString();
            this.userEmail = userEmail;
            this.type = type;
            this.message = message;
            this.reading = reading;
            this.prediction = prediction;
            this.createdAt = createdAt;
        }
        
        @Override
        public String toString() {
            return String.format("Alert[%s] %s: %s (Risk: %.1f%%)", 
                type, userEmail, message, prediction.riskScore);
        }
    }
    
    public static class HealthTrends {
        public final String userEmail;
        public final String status;
        public final Map<String, TrendData> trends;
        
        public HealthTrends(String userEmail, String status, Map<String, TrendData> trends) {
            this.userEmail = userEmail;
            this.status = status;
            this.trends = trends;
        }
    }
    
    public static class TrendData {
        public final double trend; // -1, 0, or 1
        public final String description;
        
        public TrendData(double trend, String description) {
            this.trend = trend;
            this.description = description;
        }
    }
    
    public enum AlertType {
        CRITICAL("Critical"),
        HIGH_RISK("High Risk"),
        ANOMALY("Anomaly"),
        TREND("Trend");
        
        private final String description;
        AlertType(String description) { this.description = description; }
        @Override
        public String toString() { return description; }
    }
}
