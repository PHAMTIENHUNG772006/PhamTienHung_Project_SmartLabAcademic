package com.re.service;

import com.re.model.entity.Lecturer;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface LecturerService {

    Optional<Lecturer> findById(Long id);

    Optional<Lecturer> findByUserId(Long userId);

    List<Lecturer> findAll();

    List<Lecturer> findLecturerByDepartmentId(Long departmentId);

    Lecturer save(Lecturer lecturer);
}