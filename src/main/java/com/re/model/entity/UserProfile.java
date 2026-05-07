package com.re.model.entity;

import com.re.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Table(name = "user_profiles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phone;
    private String address;
    private LocalDate dateOfBirth;
    private String gender;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
