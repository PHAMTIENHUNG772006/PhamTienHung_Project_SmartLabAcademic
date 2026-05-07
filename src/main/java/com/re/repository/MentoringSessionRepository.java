package com.re.repository;


import com.re.model.entity.MentoringSession;
import com.re.model.entity.User;
import com.re.model.entity.Lecturer;
import com.re.model.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long> {
    List<MentoringSession> findByStudentOrderByStartTimeDesc(User student);

    List<MentoringSession> findByLecturerIdAndStatus(Long lecturerId, SessionStatus status);

    List<MentoringSession> findByLecturerIdAndStatusIn(Long lecturerId, Collection<SessionStatus> statuses);

}
