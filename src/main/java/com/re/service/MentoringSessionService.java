package com.re.service;

import com.re.model.dto.BookingProcessDTO;
import com.re.model.entity.AcademicEvaluation;
import com.re.model.entity.MentoringSession;
import com.re.model.entity.User;
import com.re.model.enums.SessionStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface MentoringSessionService {

    void createBookingRequest(BookingProcessDTO dto, User student);

    Optional<MentoringSession> findById(Long id);

    List<MentoringSession> getStudentHistory(Long studentId);

    void completeMentoringProcess(Long sessionId, AcademicEvaluation evaluationData, Long equipmentId);

    List<MentoringSession> findByLecturerAndStatus(Long lecturerId, SessionStatus status);

    void approveSession(Long sessionId, Long labId, String note, List<Long> equipmentId);

    void rejectSession(Long sessionId);

    List<MentoringSession> getActiveSessionsForLecturer(Long lecturerId);

    List<MentoringSession> findAll();

    void cancelSession(Long sessionId, Long userId);

    List<MentoringSession> findByStudentIdAndStatus(Long studentId, SessionStatus status);

    List<User> getStudentsByLecturer(Long lecturerId);

    List<MentoringSession> findByStudentId(Long studentId);
}