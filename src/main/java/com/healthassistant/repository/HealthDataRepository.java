package com.healthassistant.repository;

import com.healthassistant.model.HealthData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HealthDataRepository extends JpaRepository<HealthData, Long> {
    List<HealthData> findByUserEmail(String email);
    List<HealthData> findByUserId(Long userId);
}
