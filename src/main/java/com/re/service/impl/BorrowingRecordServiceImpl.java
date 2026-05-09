package com.re.service.impl;

import com.re.model.entity.BorrowingDetail;
import com.re.model.entity.BorrowingRecord;
import com.re.model.entity.Equipment;
import com.re.model.entity.MentoringSession;
import com.re.model.enums.BorrowingStatus;
import com.re.model.enums.SessionStatus;
import com.re.repository.BorrowingRecordRepository;
import com.re.repository.EquipmentRepository;
import com.re.repository.MentoringSessionRepository;
import com.re.service.BorrowwingRecordService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowingRecordServiceImpl implements BorrowwingRecordService {

    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private MentoringSessionRepository sessionRepository;

    @Override
    @Transactional
    public BorrowingRecord confirmBorrowing(Long borrowingId) {
        BorrowingRecord record = borrowingRecordRepository.findById(borrowingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu mượn"));

        // 1. Cập nhật phiếu mượn
        record.setStatus(BorrowingStatus.BORROWED);
        record.setBorrowedAt(LocalDateTime.now());

        // 2. Cập nhật Session tương ứng để Giảng viên có thể "Hoàn tất"
        MentoringSession session = record.getSession();
        if (session != null) {
            session.setStatus(SessionStatus.APPROVED);
            sessionRepository.save(session);
        }

        return borrowingRecordRepository.save(record);
    }

    @Override
    @Transactional
    public BorrowingRecord cancelBorrowing(Long borrowingId) {
        BorrowingRecord record = borrowingRecordRepository.findById(borrowingId).orElse(null);

        if (record.getStatus() == BorrowingStatus.RETURNED || record.getStatus() == BorrowingStatus.BORROWED) {
            throw new IllegalStateException("Không thể hủy phiếu đã xuất kho hoặc đã trả");
        }

        record.setStatus(BorrowingStatus.CANCELLED);
        return borrowingRecordRepository.save(record);
    }

    @Override
    @Transactional
    public BorrowingRecord returnEquipment(Long borrowingId) {
        BorrowingRecord record = borrowingRecordRepository.findById(borrowingId).orElse(null);

        if (record == null) {
            throw new IllegalStateException("không thấy phiếu mượn thiết bị");
        }

        if (record.getStatus() != BorrowingStatus.BORROWED) {
            throw new IllegalStateException("Chỉ có thể trả thiết bị đang ở trạng thái BORROWED");
        }

        // 1. Cập nhật trạng thái phiếu
        record.setStatus(BorrowingStatus.RETURNED);

        List<BorrowingDetail> details = record.getDetails();
        for (BorrowingDetail detail : details) {
            Equipment equipment = detail.getEquipment();
            equipment.setAvailableQuantity(equipment.getAvailableQuantity() + detail.getQuantity());
            equipmentRepository.save(equipment);
        }

        return borrowingRecordRepository.save(record);
    }

    @Override
    public Optional<BorrowingRecord> findById(Long borrowingId) {
        return borrowingRecordRepository.findById(borrowingId);
    }

    @Override
    public List<BorrowingRecord> findAll() {
        return borrowingRecordRepository.findAll();
    }
}