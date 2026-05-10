package com.re.service;

import com.re.model.entity.Lab;
import com.re.model.enums.LabStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LabService {
    List<Lab> findAll();
    Page<Lab> findAll(Pageable pageable);
    List<Lab> findLabByStatus(LabStatus status);
    Optional<Lab> findById(Long id);
}
