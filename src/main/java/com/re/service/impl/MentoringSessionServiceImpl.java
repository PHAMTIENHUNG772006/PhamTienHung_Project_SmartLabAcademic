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

    private final MentoringSessionRepository sessionRepository;
    private final LecturerRepository lecturerRepository;
    private final LabsRepository labRepository;
    private final AcademicEvaluationRepository evaluationRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;
    private final EquipmentRepository equipmentRepository;
    private final BorrowingDetailRepository borrowingDetailRepository;
    private final MentoringSessionRepository mentoringSessionRepository;

    public MentoringSessionServiceImpl(MentoringSessionRepository sessionRepository,
                          LecturerRepository lecturerRepository,
                          LabsRepository labRepository,
                          AcademicEvaluationRepository evaluationRepository,
                          BorrowingRecordRepository borrowingRecordRepository,
                          EquipmentRepository equipmentRepository,
                          BorrowingDetailRepository borrowingDetailRepository,
                          MentoringSessionRepository mentoringSessionRepository) {
        this.sessionRepository = sessionRepository;
        this.lecturerRepository = lecturerRepository;
        this.labRepository = labRepository;
        this.evaluationRepository = evaluationRepository;
        this.borrowingRecordRepository = borrowingRecordRepository;
        this.equipmentRepository = equipmentRepository;
        this.borrowingDetailRepository = borrowingDetailRepository;
        this.mentoringSessionRepository = mentoringSessionRepository;
    }
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
    public List<MentoringSession> findByLecturerAndStatus(Long lecturerId, SessionStatus status) {
        return sessionRepository.findByLecturer_IdAndStatus(lecturerId, status);
    }



    @Override
    @Transactional
    public void approveSession(Long sessionId, Long labId, String note, List<Long> equipmentIds) {
        // 1. Tìm buổi mentoring
        MentoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn"));

        // 2. Tìm phòng Lab
        Lab lab = labRepository.findById(labId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng Lab"));

        session.setLab(lab);
        session.setNote(note);

        // 3. Xử lý cấp phát thiết bị nếu có danh sách ID
        if (equipmentIds != null && !equipmentIds.isEmpty()) {

            session.setStatus(SessionStatus.AWAITING_EQUIPMENT);
            BorrowingRecord record = BorrowingRecord.builder()
                    .session(session)
                    .status(BorrowingStatus.PENDING)
                    .build();
            borrowingRecordRepository.save(record);

            // Lặp qua danh sách ID thiết bị được chọn
            for (Long id : equipmentIds) {
                Equipment equipment = equipmentRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị ID: " + id));

                // Kiểm tra số lượng tồn kho trước khi trừ
                if (equipment.getAvailableQuantity() < 1) {
                    throw new RuntimeException("Thiết bị " + equipment.getName() + " đã hết hàng.");
                }

                // Tạo chi tiết mượn (BorrowingDetail)
                BorrowingDetail detail = BorrowingDetail.builder()
                        .borrowingRecord(record)
                        .equipment(equipment)
                        .quantity(1)
                        .build();


                equipment.setAvailableQuantity(equipment.getAvailableQuantity() - 1);
                equipmentRepository.save(equipment);
                borrowingDetailRepository.save(detail);
            }
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

        List<BorrowingRecord> FindBorrowingRecord =
                borrowingRecordRepository.findBySession_Id(sessionId);

        if (FindBorrowingRecord != null && !FindBorrowingRecord.isEmpty()) {
            boolean hasCancelledRecord = FindBorrowingRecord.stream()
                    .anyMatch(record ->
                            record.getStatus() == BorrowingStatus.CANCELLED);

            if (hasCancelledRecord) {
                throw new RuntimeException(
                        "Không thể hoàn thành buổi tư vấn vì phiếu mượn thiết bị đã bị hủy.");
            }
        }

        // Chỉ hoàn tất khi đã được duyệt
        if (session.getStatus() != SessionStatus.APPROVED) {
            throw new RuntimeException("Chỉ có thể hoàn thành ca tư vấn đã được duyệt thiết bị!");
        }

        // 1. Cập nhật trạng thái Session
        session.setStatus(SessionStatus.COMPLETED);
        sessionRepository.save(session);

        // 2. Lưu đánh giá
        evaluationData.setSession(session);
        evaluationData.setCreatedAt(LocalDateTime.now());
        evaluationData.setStatus(true);
        evaluationRepository.save(evaluationData);

        session.setAcademicEvaluation(evaluationData);

        // 3. Logic trả thiết bị :

        List<BorrowingRecord> records = borrowingRecordRepository.findBySession_Id(sessionId);

        if (records != null && !records.isEmpty()) {
            for (BorrowingRecord record : records) {
                // Chỉ trả những phiếu chưa được trả (PENDING hoặc BORROWED)
                if (record.getStatus() == BorrowingStatus.PENDING || record.getStatus() == BorrowingStatus.BORROWED) {

                    // Lấy danh sách thiết bị trong phiếu này và xử lý hoàn kho
                    List<BorrowingDetail> details = record.getDetails();
                    if (details != null) {
                        for (BorrowingDetail detail : details) {
                            Equipment equipment = detail.getEquipment();

                            // Cộng lại số lượng vào kho
                            equipment.setAvailableQuantity(equipment.getAvailableQuantity() + detail.getQuantity());
                            equipmentRepository.save(equipment);
                        }
                    }

                    // Cập nhật trạng thái phiếu sau khi đã hoàn kho toàn bộ thiết bị trong List
                    record.setStatus(BorrowingStatus.RETURNED);
                    borrowingRecordRepository.save(record);
                }
            }
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

    @Transactional
    public void cancelSession(Long sessionId, Long userId) {
        MentoringSession session = mentoringSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy buổi tư vấn."));

        // Kiểm tra quyền sở hữu
        if (!session.getStudent().getUserId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy lịch này.");
        }

        // Chỉ cho phép hủy khi đang chờ duyệt
        if (session.getStatus() != SessionStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể hủy lịch khi đang ở trạng thái Chờ duyệt.");
        }

        session.setStatus(SessionStatus.CANCELLED);
        session.setNote("Sinh viên tự hủy yêu cầu.");
        mentoringSessionRepository.save(session);
    }

    @Override
    public List<MentoringSession> findByStudentIdAndStatus(Long studentId, SessionStatus status) {

        return mentoringSessionRepository.findByStudent_UserIdAndStatus(studentId,status);
    }


    // Trong MentoringSessionServiceImpl
    @Override
    public List<User> getStudentsByLecturer(Long lecturerId) {
        return mentoringSessionRepository.findStudentsByLecturerId(lecturerId);
    }

    @Override
    public List<MentoringSession> findByStudentId(Long studentId) {
        return mentoringSessionRepository.findByStudent_UserId(studentId);
    }
}