package com.re.repository;

import com.re.model.entity.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    Optional<Lecturer> findById(Long id);

    Optional<Lecturer> findByUser_UserId(Long userId);

    List<Lecturer> findLecturerByDepartmentId(Long id);
}