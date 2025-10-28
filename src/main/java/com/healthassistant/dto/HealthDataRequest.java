package com.healthassistant.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public class HealthDataRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 150, message = "Age must be at most 150")
    private Integer age;

    private String gender;

    @NotNull(message = "Height is required")
    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 300, message = "Height must be at most 300 cm")
    private Double height;

    @NotNull(message = "Weight is required")
    @Min(value = 10, message = "Weight must be at least 10 kg")
    @Max(value = 500, message = "Weight must be at most 500 kg")
    private Double weight;

    @Min(value = 50, message = "Systolic BP must be at least 50")
    @Max(value = 300, message = "Systolic BP must be at most 300")
    private Integer systolicBP;

    @Min(value = 30, message = "Diastolic BP must be at least 30")
    @Max(value = 200, message = "Diastolic BP must be at most 200")
    private Integer diastolicBP;

    @Min(value = 30, message = "Heart rate must be at least 30")
    @Max(value = 200, message = "Heart rate must be at most 200")
    private Integer heartRate;

    @Min(value = 0, message = "Cholesterol must be non-negative")
    @Max(value = 1000, message = "Cholesterol must be at most 1000")
    private Double cholesterol;

    @Min(value = 0, message = "Blood sugar must be non-negative")
    @Max(value = 1000, message = "Blood sugar must be at most 1000")
    private Double bloodSugar;

    private Boolean hasDiabetes;
    private Boolean hasHypertension;
    private Boolean hasHeartDisease;
    private Boolean smokes;
    private Boolean exercisesRegularly;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

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
}
