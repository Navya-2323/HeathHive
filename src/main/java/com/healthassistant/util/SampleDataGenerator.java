package com.healthassistant.util;

import com.healthassistant.dto.HealthDataRequest;

public class SampleDataGenerator {

    public static HealthDataRequest createLowRiskSample() {
        HealthDataRequest request = new HealthDataRequest();
        request.setName("Alice Johnson");
        request.setEmail("alice.johnson@example.com");
        request.setAge(28);
        request.setGender("FEMALE");
        request.setHeight(165.0);
        request.setWeight(60.0);
        request.setSystolicBP(110);
        request.setDiastolicBP(70);
        request.setHeartRate(65);
        request.setCholesterol(160.0);
        request.setBloodSugar(85.0);
        request.setHasDiabetes(false);
        request.setHasHypertension(false);
        request.setHasHeartDisease(false);
        request.setSmokes(false);
        request.setExercisesRegularly(true);
        return request;
    }

    public static HealthDataRequest createMediumRiskSample() {
        HealthDataRequest request = new HealthDataRequest();
        request.setName("Bob Smith");
        request.setEmail("bob.smith@example.com");
        request.setAge(45);
        request.setGender("MALE");
        request.setHeight(180.0);
        request.setWeight(90.0);
        request.setSystolicBP(135);
        request.setDiastolicBP(85);
        request.setHeartRate(80);
        request.setCholesterol(220.0);
        request.setBloodSugar(110.0);
        request.setHasDiabetes(false);
        request.setHasHypertension(false);
        request.setHasHeartDisease(false);
        request.setSmokes(false);
        request.setExercisesRegularly(false);
        return request;
    }

    public static HealthDataRequest createHighRiskSample() {
        HealthDataRequest request = new HealthDataRequest();
        request.setName("Charlie Brown");
        request.setEmail("charlie.brown@example.com");
        request.setAge(65);
        request.setGender("MALE");
        request.setHeight(170.0);
        request.setWeight(95.0);
        request.setSystolicBP(150);
        request.setDiastolicBP(95);
        request.setHeartRate(90);
        request.setCholesterol(280.0);
        request.setBloodSugar(140.0);
        request.setHasDiabetes(true);
        request.setHasHypertension(true);
        request.setHasHeartDisease(false);
        request.setSmokes(true);
        request.setExercisesRegularly(false);
        return request;
    }
}
