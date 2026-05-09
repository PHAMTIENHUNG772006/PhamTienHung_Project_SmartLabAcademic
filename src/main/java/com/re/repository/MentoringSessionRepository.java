package com.re.repository;

import com.re.model.entity.MentoringSession;
import com.re.model.entity.User;
import com.re.model.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MentoringSessionRepository extends JpaRepository<MentoringSession, Long> {


    List<MentoringSession> findByStudent_UserIdOrderByStartTimeDesc(Long userId);

    // Tìm theo Giảng viên và Trạng thái (Đã dùng cho phần Lecturer)
    List<MentoringSession> findByLecturer_IdAndStatus(Long lecturerId, SessionStatus status);

    List<MentoringSession> findByLecturer_IdAndStatusIn(Long lecturerId, Collection<SessionStatus> statuses);

    @Query("SELECT s FROM MentoringSession s WHERE s.lecturer.id = :lecturerId " +
            "AND s.status IN (com.re.model.enums.SessionStatus.PENDING, " +
            "                 com.re.model.enums.SessionStatus.APPROVED, " +
            "                 com.re.model.enums.SessionStatus.AWAITING_EQUIPMENT) " +
            "AND ((s.startTime < :endTime AND s.endTime > :startTime))")
    List<MentoringSession> findConflictingSessions(
            @Param("lecturerId") Long lecturerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    Optional<MentoringSession> findById(Long id);

}