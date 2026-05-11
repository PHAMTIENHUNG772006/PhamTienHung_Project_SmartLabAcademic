package com.re.service;

import com.re.model.entity.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface EquimentService {
    List<Equipment> findAll();
    Page<Equipment> findAll(Pageable pageable);
    Equipment save(Equipment equipment);
    Equipment findById(Long id);
    void delete(Long id);
    Page<Equipment> findAllActive(Pageable pageable);
}