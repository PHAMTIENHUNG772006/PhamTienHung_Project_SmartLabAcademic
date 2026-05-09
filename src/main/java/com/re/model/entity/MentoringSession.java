package com.re.model.entity;

import com.re.model.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "mentoring_sessions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MentoringSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    private String topic;
    private String note;

    @ManyToOne
    @JoinColumn(name = "lab_id")
    private Lab lab;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY)
    private List<BorrowingRecord> borrowingRecords;

    @OneToOne
    @JoinColumn(name = "academic_evaluation_id")
    private AcademicEvaluation academicEvaluation;
}