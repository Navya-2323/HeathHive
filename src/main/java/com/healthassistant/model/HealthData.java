package com.healthassistant.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_data")
public class HealthData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Height is required")
    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 300, message = "Height must be at most 300 cm")
    private Double height; // in cm

    @NotNull(message = "Weight is required")
    @Min(value = 10, message = "Weight must be at least 10 kg")
    @Max(value = 500, message = "Weight must be at most 500 kg")
    private Double weight; // in kg

    @Min(value = 50, message = "Systolic BP must be at least 50")
    @Max(value = 300, message = "Systolic BP must be at most 300")
    private Integer systolicBP; // mmHg

    @Min(value = 30, message = "Diastolic BP must be at least 30")
    @Max(value = 200, message = "Diastolic BP must be at most 200")
    private Integer diastolicBP; // mmHg

    @Min(value = 30, message = "Heart rate must be at least 30")
    @Max(value = 200, message = "Heart rate must be at most 200")
    private Integer heartRate; // bpm

    @Min(value = 0, message = "Cholesterol must be non-negative")
    @Max(value = 1000, message = "Cholesterol must be at most 1000")
    private Double cholesterol; // mg/dL

    @Min(value = 0, message = "Blood sugar must be non-negative")
    @Max(value = 1000, message = "Blood sugar must be at most 1000")
    private Double bloodSugar; // mg/dL

    private Boolean hasDiabetes;
    private Boolean hasHypertension;
    private Boolean hasHeartDisease;
    private Boolean smokes;
    private Boolean exercisesRegularly;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    public HealthData() {
        this.submittedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Integer getSystolicBP() { return systolicBP; }
    public void setSystolicBP(Integer systolicBP) { this.systolicBP = systolicBP; }

    public Integer getDiastolicBP() { return diastolicBP; }
    public void setDiastolicBP(Integer diastolicBP) { this.diastolicBP = diastolicBP; }

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Double getCholesterol() { return cholesterol; }
    public void setCholesterol(Double cholesterol) { this.cholesterol = cholesterol; }

    public Double getBloodSugar() { return bloodSugar; }
    public void setBloodSugar(Double bloodSugar) { this.bloodSugar = bloodSugar; }

    public Boolean getHasDiabetes() { return hasDiabetes; }
    public void setHasDiabetes(Boolean hasDiabetes) { this.hasDiabetes = hasDiabetes; }

    public Boolean getHasHypertension() { return hasHypertension; }
    public void setHasHypertension(Boolean hasHypertension) { this.hasHypertension = hasHypertension; }

    public Boolean getHasHeartDisease() { return hasHeartDisease; }
    public void setHasHeartDisease(Boolean hasHeartDisease) { this.hasHeartDisease = hasHeartDisease; }

    public Boolean getSmokes() { return smokes; }
    public void setSmokes(Boolean smokes) { this.smokes = smokes; }

    public Boolean getExercisesRegularly() { return exercisesRegularly; }
    public void setExercisesRegularly(Boolean exercisesRegularly) { this.exercisesRegularly = exercisesRegularly; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    // Helper method to calculate BMI
    public Double getBMI() {
        if (height != null && weight != null && height > 0) {
            double heightInMeters = height / 100.0;
            return weight / (heightInMeters * heightInMeters);
        }
        return null;
    }
}
