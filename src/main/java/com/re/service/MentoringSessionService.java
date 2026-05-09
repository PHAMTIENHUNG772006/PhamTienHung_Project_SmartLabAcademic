package com.re.service;

import com.re.model.dto.BookingProcessDTO;
import com.re.model.entity.AcademicEvaluation;
import com.re.model.entity.MentoringSession;
import com.re.model.entity.User;
import com.re.model.enums.SessionStatus;

import java.util.List;
import java.util.Optional;

public interface MentoringSessionService {

    void createBookingRequest(BookingProcessDTO dto, User student);

    Optional<MentoringSession> findById(Long id);

    List<MentoringSession> getStudentHistory(Long studentId);

    List<MentoringSession> getPendingSessionsForLecturer(Long lecturerId);

    void completeMentoringProcess(Long sessionId, AcademicEvaluation evaluationData, Long equipmentId);

    List<MentoringSession> findByLecturerAndStatus(Long lecturerId, SessionStatus status);

    void approveSession(Long sessionId, Long labId, String note, Long equipmentId);

    void rejectSession(Long sessionId);

    List<MentoringSession> getActiveSessionsForLecturer(Long lecturerId);
}