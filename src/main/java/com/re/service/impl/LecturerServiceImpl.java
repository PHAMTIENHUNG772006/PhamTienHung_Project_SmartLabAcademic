package com.re.service.impl;

import com.re.model.entity.Lecturer;
import com.re.repository.LecturerRepository;
import com.re.repository.LecturerTopConsulting;
import com.re.service.LecturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LecturerServiceImpl implements LecturerService {

    @Autowired
    private LecturerRepository lecturerRepository;

    @Override
    public Optional<Lecturer> findById(Long id) {
        return lecturerRepository.findById(id);
    }

    @Override
    public Optional<Lecturer> findByUserId(Long userId) {
        return lecturerRepository.findByUser_UserId(userId);
    }

    @Override
    public List<Lecturer> findAll() {
        return lecturerRepository.findAll();
    }

    @Override
    public List<Lecturer> findLecturerByDepartmentId(Long departmentId) {
        return lecturerRepository.findLecturerByDepartmentId(departmentId);
    }

    @Override
    public Lecturer save(Lecturer lecturer) {
        return lecturerRepository.save(lecturer);
    }

    @Override
    public LecturerTopConsulting getMostActiveLecturer() {
        Pageable topOne = PageRequest.of(0, 1);
        List<LecturerTopConsulting> results = lecturerRepository.findTopLecturerBySessions(topOne);
        return results.isEmpty() ? null : results.get(0);
    }
}