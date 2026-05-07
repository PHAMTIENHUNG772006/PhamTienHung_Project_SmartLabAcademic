package com.re.repository;

import com.re.model.entity.Lecturer;
import com.re.model.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface LecturerRepository extends JpaRepository<Lecturer,Long> {
  Lecturer findLecturerById(Long id);
    List<Lecturer> findAll();
    List<Lecturer> findLecturerByDepartmentId(Long id);

}
