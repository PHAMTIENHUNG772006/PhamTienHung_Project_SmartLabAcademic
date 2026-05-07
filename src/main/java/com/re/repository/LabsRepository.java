package com.re.repository;

import com.re.model.entity.Department;
import com.re.model.entity.Lab;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LabsRepository extends JpaRepository<Lab,Long> {
    List<Lab> findLabByName(String name);
    Optional<Lab> findById(Long id);
    List<Lab> findAll();
}