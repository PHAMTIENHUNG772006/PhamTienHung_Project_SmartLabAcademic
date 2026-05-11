package com.re.service.impl;

import com.re.model.entity.Lab;
import com.re.model.enums.LabStatus;
import com.re.repository.LabsRepository;
import com.re.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LabServiceImpl implements LabService {

    private final LabsRepository labRepository;

    public LabServiceImpl(LabsRepository labRepository) {
        this.labRepository = labRepository;
    }


    @Override
    public List<Lab> findAll() {
        return labRepository.findAll();
    }

    @Override
    public Page<Lab> findAll(Pageable pageable) {
        return labRepository.findAll(pageable);
    }

    @Override
    public List<Lab> findLabByStatus(LabStatus status) {
        return labRepository.findLabByStatus(status);
    }

    @Override
    public Optional<Lab> findById(Long id) {
        return labRepository.findById(id);
    }
}
