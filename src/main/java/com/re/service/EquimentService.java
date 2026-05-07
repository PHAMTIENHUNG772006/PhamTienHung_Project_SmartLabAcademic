package com.re.service;

import com.re.model.entity.Equipment;

import java.util.List;

public interface EquimentService {
    List<Equipment> findAll();
    Equipment save(Equipment equipment);
    Equipment findById(Long id);
    Equipment update(Equipment equipment);
    void delete(Long id);
}
