package com.re.service.impl;

import com.re.model.entity.Lab;
import com.re.repository.LabsRepository;
import com.re.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LabServiceImpl implements LabService {

    @Autowired
    private LabsRepository labRepository;


    @Override
    public List<Lab> findAll() {
        return labRepository.findAll();
    }

    @Override
    public List<Lab> findLabByName(String name) {
        return labRepository.findLabByName(name);
    }

    @Override
    public Optional<Lab> findById(Long id) {
        return labRepository.findById(id);
    }
}
