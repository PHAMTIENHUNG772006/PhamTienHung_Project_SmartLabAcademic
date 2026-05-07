package com.re.model.entity;

import com.re.model.enums.LabStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "labs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer capacity;

    private String location;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private LabStatus status;
}
