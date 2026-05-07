package com.re.repository;

import com.re.model.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquimentRepository extends JpaRepository<Equipment,Long> {
}
