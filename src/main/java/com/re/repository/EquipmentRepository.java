package com.re.repository;

import com.re.model.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment,Long> {
    @Query("SELECT COUNT(bd) FROM BorrowingDetail bd " +
            "WHERE bd.equipment.id = :equipmentId " +
            "AND bd.borrowingRecord.status NOT IN (com.re.model.enums.BorrowingStatus.RETURNED, com.re.model.enums.BorrowingStatus.CANCELLED)")
    long countActiveBorrowing(@Param("equipmentId") Long equipmentId);
}
