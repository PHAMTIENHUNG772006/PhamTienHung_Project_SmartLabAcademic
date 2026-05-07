package com.re.model.entity;

import com.re.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "borrowing_details")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BorrowingDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "borrowing_id")
    private BorrowingRecord borrowingRecord;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;
}