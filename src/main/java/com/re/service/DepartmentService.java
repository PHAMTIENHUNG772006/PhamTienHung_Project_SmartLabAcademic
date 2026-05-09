package com.re.service;

import com.re.model.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    Page<Department> findAll(Pageable pageable);
    List<Department> findAll();
    List<Department> findDepartmentByName(String name);
    List<Department> findDepartmentById(Long id);
    Optional<Department> findById(Long id);
}
