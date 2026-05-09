package com.re.model.entity;

import com.re.model.enums.BorrowingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "borrowing_records")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BorrowingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime borrowedAt;
    private LocalDateTime returnedAt;

    @Enumerated(EnumType.STRING)
    private BorrowingStatus status;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private MentoringSession session;

    @OneToMany(mappedBy = "borrowingRecord")
    private List<BorrowingDetail> details;
}
