package com.re.model.entity;

import com.re.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "academic_evaluations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AcademicEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String result;
    private Double score;
    private String feedback;
    private LocalDateTime createdAt;
    private Boolean status;

    @OneToOne
    @JoinColumn(name = "session_id")
    private MentoringSession session;
}