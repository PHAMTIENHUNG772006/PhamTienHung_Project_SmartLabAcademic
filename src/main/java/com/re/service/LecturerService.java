package com.re.service;

import com.re.model.entity.Lecturer;

import java.util.List;

public interface LecturerService{
    List<Lecturer> findAll();
    Lecturer findLecturerById(Long id);
    List<Lecturer> findLecturerByDepartmentId(Long id);
}
