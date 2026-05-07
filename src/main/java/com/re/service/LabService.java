package com.re.service;

import com.re.model.entity.Lab;

import java.util.List;
import java.util.Optional;

public interface LabService {
    List<Lab> findAll();
    List<Lab> findLabByName(String name);
    Optional<Lab> findById(Long id);
}
