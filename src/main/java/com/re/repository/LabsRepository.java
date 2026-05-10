package com.re.repository;

import com.re.model.entity.Department;
import com.re.model.entity.Lab;
import com.re.model.enums.LabStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LabsRepository extends JpaRepository<Lab,Long> {
    List<Lab> findLabByName(String name);
    List<Lab> findLabByStatus (LabStatus status);
}