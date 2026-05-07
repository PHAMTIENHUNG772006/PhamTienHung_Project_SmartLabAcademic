package com.re.service.impl;

import com.re.model.entity.Lecturer;
import com.re.repository.LecturerRepository;
import com.re.service.LecturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class LecturerServiceImpl implements LecturerService {

    @Autowired
    private LecturerRepository lecturerRepository;

    @Override
    public List<Lecturer> findAll() {
        return lecturerRepository.findAll();
    }

    @Override
    public Lecturer findLecturerById(Long id) {
        return lecturerRepository.findLecturerById(id);
    }

    @Override
    public List<Lecturer> findLecturerByDepartmentId(Long id) {
        return lecturerRepository.findLecturerByDepartmentId(id);
    }
}
