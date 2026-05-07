package com.re.service.impl;

import com.re.model.dto.BookingProcessDTO;
import com.re.model.entity.Lab;
import com.re.model.entity.MentoringSession;
import com.re.model.entity.User;
import com.re.model.enums.SessionStatus;
import com.re.repository.EquimentRepository;
import com.re.repository.LabsRepository;
import com.re.repository.MentoringSessionRepository;
import com.re.repository.LecturerRepository;
import com.re.service.MentoringSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MentoringSessionServiceImpl implements MentoringSessionService {

    @Autowired
    private MentoringSessionRepository sessionRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private LabsRepository labRepository;


    @Override
    @Transactional
    public void createBookingRequest(BookingProcessDTO dto, User student) {
        MentoringSession session = MentoringSession.builder()
                .startTime(dto.getBookingDate())
                .endTime(dto.getBookingDate().plusHours(1))
                .topic(dto.getReason())
                .status(SessionStatus.PENDING)
                .student(student)
                .lecturer(lecturerRepository.findById(dto.getLecturerId()).orElse(null))
                .build();

        sessionRepository.save(session);
    }

    @Override
    public List<MentoringSession> getStudentHistory(User student) {
        return sessionRepository.findByStudentOrderByStartTimeDesc(student);
    }

    @Override
    public List<MentoringSession> getPendingSessionsForLecturer(Long lecturerId) {
        return sessionRepository.findByLecturerIdAndStatus(lecturerId, SessionStatus.PENDING);
    }

    @Override
    public List<MentoringSession> findByLecturerAndStatus(Long lecturerId, SessionStatus status) {
        // Sử dụng lại chính logic mà Hưng đã có ở hàm getPending
        return sessionRepository.findByLecturerIdAndStatus(lecturerId, status);
    }

    @Override
    @Transactional
    public void approveSession(Long sessionId, Long labId, String note) {
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn ID: " + sessionId));

        Lab lab = labRepository.findById(labId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng Lab ID: " + labId));

        // 3. Cập nhật thông tin
        session.setStatus(SessionStatus.APPROVED);
//        session.set(lab);
        // Nếu Entity của Hưng có trường note hoặc feedback, hãy set nó ở đây
        // session.setLecturerNote(note);

        sessionRepository.save(session);
    }

    @Override
    @Transactional
    public void rejectSession(Long sessionId) {
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn ID: " + sessionId));

        session.setStatus(SessionStatus.REJECTED);
        sessionRepository.save(session);
    }
}