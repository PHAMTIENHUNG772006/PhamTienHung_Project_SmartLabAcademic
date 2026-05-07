package com.re.service.impl;

import com.re.model.entity.Department;
import com.re.repository.DepartmentRepository;
import com.re.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {


    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public List<Department> findDepartmentByName(String name) {
        return departmentRepository.findDepartmentByName(name);
    }

    @Override
    public List<Department> findDepartmentById(Long id) {
        return departmentRepository.findDepartmentById(id);
    }

    @Override
    public Optional<Department> findById(Long id) {
        return departmentRepository.findById(id);
    }
}
