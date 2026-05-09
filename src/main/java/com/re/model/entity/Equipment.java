package com.re.model.entity;

import com.re.model.enums.EquipmentCategory;
import com.re.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "equipments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer availableQuantity;
    private Boolean status;

    @Enumerated(EnumType.STRING)
    private EquipmentCategory category;

    @OneToMany(mappedBy = "equipment")
    private List<BorrowingDetail> borrowingDetails;
}