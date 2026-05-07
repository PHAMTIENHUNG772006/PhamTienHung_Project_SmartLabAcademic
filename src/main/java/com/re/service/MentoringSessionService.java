package com.re.service;

import com.re.model.dto.BookingProcessDTO;
import com.re.model.entity.MentoringSession;
import com.re.model.entity.User;
import com.re.model.enums.SessionStatus;

import java.util.List;

public interface MentoringSessionService {

    void createBookingRequest(BookingProcessDTO dto, User student);

    List<MentoringSession> getStudentHistory(User student);

    List<MentoringSession> getPendingSessionsForLecturer(Long lecturerId);


    List<MentoringSession> findByLecturerAndStatus(Long lecturerId, SessionStatus status);

    void approveSession(Long sessionId, Long labId, String note);

    void rejectSession(Long sessionId);
}