package com.re.repository;

import com.re.model.entity.Lecturer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    Optional<Lecturer> findById(Long id);

    Optional<Lecturer> findByUser_UserId(Long userId);

    List<Lecturer> findLecturerByDepartmentId(Long id);

    @Query("SELECT m.lecturer AS lecturer, COUNT(m) AS sessionCount " +
            "FROM MentoringSession m " +
            "GROUP BY m.lecturer " +
            "ORDER BY COUNT(m) DESC")
    List<LecturerTopConsulting> findTopLecturerBySessions(Pageable pageable);
}