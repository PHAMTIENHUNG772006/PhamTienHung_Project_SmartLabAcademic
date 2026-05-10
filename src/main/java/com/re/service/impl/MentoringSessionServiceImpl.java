package com.re.service.impl;

import com.re.model.dto.BookingProcessDTO;
import com.re.model.entity.*;
import com.re.model.enums.BorrowingStatus;
import com.re.model.enums.SessionStatus;
import com.re.repository.*;
import com.re.service.MentoringSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MentoringSessionServiceImpl implements MentoringSessionService {

    @Autowired
    private MentoringSessionRepository sessionRepository;

    @Autowired
    private LecturerRepository lecturerRepository;

    @Autowired
    private LabsRepository labRepository;

    @Autowired
    private AcademicEvaluationRepository evaluationRepository;

    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private BorrowingDetailRepository borrowingDetailRepository;

    @Autowired
    private BorrowingRecordServiceImpl borrowingRecordService;


    @Override
    public List<MentoringSession> findAll() {
        return sessionRepository.findAll();
    }

    @Override
    @Transactional
    public void createBookingRequest(BookingProcessDTO dto, User student) {

        LocalDateTime startTime = dto.getBookingDate();
        LocalDateTime endTime = startTime.plusHours(1);


        List<MentoringSession> conflicts = sessionRepository.findConflictingSessions(
                dto.getLecturerId(),
                startTime,
                endTime
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Giảng viên đã có lịch bận hoặc đang có yêu cầu chờ duyệt trong khung giờ này.");
        }

        MentoringSession session = MentoringSession.builder()
                .startTime(dto.getBookingDate())
                .endTime(dto.getBookingDate().plusHours(1))
                .topic(dto.getReason())
                .status(SessionStatus.PENDING)
                .student(student)
                .lecturer(lecturerRepository.findById(dto.getLecturerId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy giảng viên")))
                .build();

        sessionRepository.save(session);
    }

    @Override
    public Optional<MentoringSession> findById(Long id) {
        return sessionRepository.findById(id);
    }


    @Override
    public List<MentoringSession> getPendingSessionsForLecturer(Long lecturerId) {
        return sessionRepository.findByLecturer_IdAndStatus(lecturerId, SessionStatus.PENDING);
    }


    @Override
    public List<MentoringSession> findByLecturerAndStatus(Long lecturerId, SessionStatus status) {
        return sessionRepository.findByLecturer_IdAndStatus(lecturerId, status);
    }

    @Override
    @Transactional
    public void approveSession(Long sessionId, Long labId, String note, Long equipmentId) {
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn"));

        Lab lab = labRepository.findById(labId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng Lab"));

        session.setLab(lab);
        session.setNote(note);

        Equipment equipment = equipmentRepository.findById(equipmentId).orElse(null);

        if (equipment != null) {

            session.setStatus(SessionStatus.AWAITING_EQUIPMENT);

            BorrowingRecord record = BorrowingRecord.builder()
                    .session(session)
                    .status(BorrowingStatus.PENDING)
                    .build();
            borrowingRecordRepository.save(record);

            BorrowingDetail detail = BorrowingDetail.builder()
                    .borrowingRecord(record)
                    .equipment(equipment)
                    .quantity(1)
                    .build();
            equipment.setAvailableQuantity(equipment.getAvailableQuantity() - 1);
            equipmentRepository.save(equipment);
            borrowingDetailRepository.save(detail);
        } else {
            session.setStatus(SessionStatus.APPROVED);
        }
        sessionRepository.save(session);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeMentoringProcess(Long sessionId, AcademicEvaluation evaluationData, Long equipmentId) {
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca tư vấn ID: " + sessionId));

        // Chỉ hoàn tất khi đã được duyệt
        if (session.getStatus() != SessionStatus.APPROVED) {
            throw new RuntimeException("Chỉ có thể hoàn thành ca tư vấn đã được duyệt!");
        }

        // 1. Cập nhật trạng thái Session
        session.setStatus(SessionStatus.COMPLETED);
        sessionRepository.save(session);

        // 2. Lưu đánh giá
        evaluationData.setSession(session);
        evaluationData.setCreatedAt(LocalDateTime.now());
        evaluationData.setStatus(true);
        evaluationRepository.save(evaluationData);

        // 3. Logic trả thiết bị :
        // Tìm phiếu mượn của Session này để hoàn trả số lượng
        Optional<BorrowingRecord> recordOpt = borrowingRecordRepository.findBySession_Id(sessionId)
                .stream()
                .filter(r -> r.getStatus() == BorrowingStatus.BORROWED || r.getStatus() == BorrowingStatus.PENDING)
                .findFirst();

        if (recordOpt.isPresent()) {
            BorrowingRecord record = recordOpt.get();
            borrowingRecordService.returnEquipment(record.getId());
            borrowingRecordRepository.save(record);
        }
    }

    @Override
    @Transactional
    public void rejectSession(Long sessionId) {
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn ID: " + sessionId));

        session.setStatus(SessionStatus.REJECTED);
        sessionRepository.saveAndFlush(session);
    }


    @Override
    public List<MentoringSession> getStudentHistory(Long studentId) {
        return sessionRepository.findByStudent_UserIdOrderByStartTimeDesc(studentId);
    }

    @Override
    public List<MentoringSession> getActiveSessionsForLecturer(Long lecturerId) {

        return sessionRepository.findByLecturer_IdAndStatusIn(
                lecturerId,
                List.of(SessionStatus.APPROVED, SessionStatus.AWAITING_EQUIPMENT)
        );
    }
}