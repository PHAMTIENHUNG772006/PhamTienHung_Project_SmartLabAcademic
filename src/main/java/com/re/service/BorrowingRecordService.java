package com.re.service;

import com.re.model.entity.BorrowingRecord;
import com.re.model.enums.BorrowingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BorrowingRecordService {
    BorrowingRecord confirmBorrowing(Long borrowingId);
    BorrowingRecord cancelBorrowing(Long borrowingId);
    BorrowingRecord returnEquipment(Long borrowingId);
    Optional<BorrowingRecord> findById(Long borrowingId);
    List<BorrowingRecord> findAll();
    Page<BorrowingRecord> findAll(Pageable pageable);
    Integer countBorrowingRecordByStatus(BorrowingStatus status);
}
