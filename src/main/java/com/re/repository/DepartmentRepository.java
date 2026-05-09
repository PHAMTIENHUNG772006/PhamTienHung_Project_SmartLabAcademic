package com.re.repository;

import com.re.model.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
    List<Department> findDepartmentByName(String name);
    List<Department> findDepartmentById(Long id);
}
