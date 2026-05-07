package com.re.service;

import com.re.model.entity.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    List<Department> findAll();
    List<Department> findDepartmentByName(String name);
    List<Department> findDepartmentById(Long id);
    Optional<Department> findById(Long id);
}
