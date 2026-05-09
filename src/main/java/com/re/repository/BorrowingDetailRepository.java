package com.re.repository;

import com.re.model.entity.BorrowingDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowingDetailRepository extends JpaRepository<BorrowingDetail, Integer> {
}
