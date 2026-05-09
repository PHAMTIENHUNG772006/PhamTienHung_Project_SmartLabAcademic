package com.re.repository;

import com.re.model.entity.BorrowingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Integer> {
    Optional<BorrowingRecord> findById(Long borrowingId);
}
