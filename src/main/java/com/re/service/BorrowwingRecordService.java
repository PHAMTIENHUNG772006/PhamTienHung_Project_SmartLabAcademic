package com.re.service;

import com.re.model.entity.BorrowingRecord;

import java.util.List;
import java.util.Optional;

public interface BorrowwingRecordService {
    BorrowingRecord confirmBorrowing(Long borrowingId);
    BorrowingRecord cancelBorrowing(Long borrowingId);
    BorrowingRecord returnEquipment(Long borrowingId);
    Optional<BorrowingRecord> findById(Long borrowingId);
    List<BorrowingRecord> findAll();
}
