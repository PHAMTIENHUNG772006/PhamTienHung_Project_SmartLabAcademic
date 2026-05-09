package com.re.repository;

import com.re.model.entity.BorrowingRecord;
import com.re.model.enums.BorrowingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Integer> {
    Optional<BorrowingRecord> findById(Long borrowingId);
    List<BorrowingRecord> findBySession_Id(Long sessionId);

    Integer countBorrowingRecordByStatus(BorrowingStatus status);
}
