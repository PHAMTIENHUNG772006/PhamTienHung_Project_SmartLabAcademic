package com.re.repository;

import com.re.model.entity.AcademicEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicEvaluationRepository extends JpaRepository<AcademicEvaluation, Long> {
}
