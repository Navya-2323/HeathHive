package com.healthassistant.repository;

import com.healthassistant.model.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {
    List<RiskAssessment> findByHealthDataUserId(Long userId);
    List<RiskAssessment> findByHealthDataUserEmail(String email);
}
