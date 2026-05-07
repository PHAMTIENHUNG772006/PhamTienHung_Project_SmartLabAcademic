package com.re.service.impl;

import com.re.model.entity.Equipment;
import com.re.repository.EquimentRepository;
import com.re.service.EquimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquimentServiceImpl implements EquimentService {


    @Autowired
    private EquimentRepository equimentRepository;


    @Override
    public List<Equipment> findAll() {
        return equimentRepository.findAll();
    }

    @Override
    public Equipment save(Equipment equipment) {
        return equimentRepository.save(equipment);
    }

    @Override
    public Equipment findById(Long id) {
        return equimentRepository.findById(id).orElse(null);
    }

    @Override
    public Equipment update(Equipment equipment) {
        return null;
    }

    @Override
    public void delete(Long id) {
        equimentRepository.deleteById(id);
    }
}
