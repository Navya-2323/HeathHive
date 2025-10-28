package com.healthassistant.ml;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Individual ML Model implementing different algorithms
 */
public class MLModel {
    
    private Map<String, Double> weights;
    private double bias;
    private boolean isTrained;
    
    public MLModel() {
        this.weights = new HashMap<>();
        this.bias = 0.0;
        this.isTrained = false;
    }
    
    /**
     * Train using Linear Regression
     */
    public void trainLinearRegression(List<AdvancedMLService.TrainingExample> trainingData) {
        if (trainingData.isEmpty()) return;
        
        // Initialize weights
        Set<String> allFeatures = trainingData.stream()
            .flatMap(example -> example.features.keySet().stream())
            .collect(Collectors.toSet());
        
        for (String feature : allFeatures) {
            weights.put(feature, Math.random() * 0.1 - 0.05); // Small random initialization
        }
        
        // Gradient descent
        double learningRate = 0.01;
        int iterations = 1000;
        
        for (int iter = 0; iter < iterations; iter++) {
            double totalError = 0.0;
            
            for (AdvancedMLService.TrainingExample example : trainingData) {
                double prediction = predict(example.features);
                double actual = riskLevelToScore(example.actualRiskLevel);
                double error = prediction - actual;
                
                totalError += error * error;
                
                // Update weights
                for (Map.Entry<String, Double> entry : example.features.entrySet()) {
                    String feature = entry.getKey();
                    double value = entry.getValue();
                    double currentWeight = weights.getOrDefault(feature, 0.0);
                    weights.put(feature, currentWeight - learningRate * error * value);
                }
                
                // Update bias
                bias -= learningRate * error;
            }
            
            // Early stopping if error is small
            if (totalError < 0.01) break;
        }
        
        isTrained = true;
    }
    
    /**
     * Train using Random Forest (simplified)
     */
    public void trainRandomForest(List<AdvancedMLService.TrainingExample> trainingData) {
        if (trainingData.isEmpty()) return;
        
        // Simplified Random Forest using decision stumps
        Set<String> allFeatures = trainingData.stream()
            .flatMap(example -> example.features.keySet().stream())
            .collect(Collectors.toSet());
        
        Random random = new Random();
        
        // Create multiple decision stumps
        for (String feature : allFeatures) {
            if (random.nextDouble() < 0.7) { // 70% chance to include feature
                double threshold = calculateOptimalThreshold(trainingData, feature);
                double weight = random.nextGaussian() * 0.5; // Random weight
                weights.put(feature, weight);
            }
        }
        
        // Calculate bias based on class distribution
        long highRiskCount = trainingData.stream()
            .mapToLong(example -> example.actualRiskLevel == AdvancedMLService.RiskLevel.HIGH ? 1 : 0)
            .sum();
        
        bias = (double) highRiskCount / trainingData.size() * 100.0;
        isTrained = true;
    }
    
    /**
     * Train using Neural Network (simplified)
     */
    public void trainNeuralNetwork(List<AdvancedMLService.TrainingExample> trainingData) {
        if (trainingData.isEmpty()) return;
        
        // Simplified neural network with one hidden layer
        Set<String> allFeatures = trainingData.stream()
            .flatMap(example -> example.features.keySet().stream())
            .collect(Collectors.toSet());
        
        Random random = new Random();
        
        // Initialize weights with Xavier initialization
        for (String feature : allFeatures) {
            double weight = random.nextGaussian() * Math.sqrt(2.0 / allFeatures.size());
            weights.put(feature, weight);
        }
        
        // Training with backpropagation (simplified)
        double learningRate = 0.001;
        int epochs = 500;
        
        for (int epoch = 0; epoch < epochs; epoch++) {
            Collections.shuffle(trainingData);
            
            for (AdvancedMLService.TrainingExample example : trainingData) {
                double prediction = predict(example.features);
                double actual = riskLevelToScore(example.actualRiskLevel);
                double error = prediction - actual;
                
                // Update weights using gradient descent
                for (Map.Entry<String, Double> entry : example.features.entrySet()) {
                    String feature = entry.getKey();
                    double value = entry.getValue();
                    double currentWeight = weights.getOrDefault(feature, 0.0);
                    
                    // Apply activation function derivative (simplified)
                    double gradient = error * value * sigmoidDerivative(prediction);
                    weights.put(feature, currentWeight - learningRate * gradient);
                }
                
                bias -= learningRate * error * sigmoidDerivative(prediction);
            }
        }
        
        isTrained = true;
    }
    
    /**
     * Make prediction
     */
    public double predict(Map<String, Double> features) {
        if (!isTrained) return 0.0;
        
        double prediction = bias;
        
        for (Map.Entry<String, Double> entry : features.entrySet()) {
            String feature = entry.getKey();
            double value = entry.getValue();
            double weight = weights.getOrDefault(feature, 0.0);
            prediction += weight * value;
        }
        
        // Apply sigmoid activation
        return sigmoid(prediction) * 100.0; // Scale to 0-100
    }
    
    /**
     * Calculate optimal threshold for a feature (for Random Forest)
     */
    private double calculateOptimalThreshold(List<AdvancedMLService.TrainingExample> trainingData, String feature) {
        List<Double> values = trainingData.stream()
            .map(example -> example.features.getOrDefault(feature, 0.0))
            .sorted()
            .collect(Collectors.toList());
        
        if (values.isEmpty()) return 0.0;
        
        // Find median as threshold
        int mid = values.size() / 2;
        return values.get(mid);
    }
    
    /**
     * Sigmoid activation function
     */
    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }
    
    /**
     * Sigmoid derivative
     */
    private double sigmoidDerivative(double x) {
        double s = sigmoid(x);
        return s * (1.0 - s);
    }
    
    /**
     * Convert risk level to numerical score
     */
    private double riskLevelToScore(AdvancedMLService.RiskLevel riskLevel) {
        switch (riskLevel) {
            case LOW: return 15.0;
            case MEDIUM: return 50.0;
            case HIGH: return 85.0;
            default: return 0.0;
        }
    }
    
    /**
     * Get feature importance
     */
    public Map<String, Double> getFeatureImportance() {
        return new HashMap<>(weights);
    }
    
    /**
     * Check if model is trained
     */
    public boolean isTrained() {
        return isTrained;
    }
}
