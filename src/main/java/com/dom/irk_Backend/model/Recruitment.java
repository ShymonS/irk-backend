package com.dom.irk_Backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@lombok.Data
@Entity
@Table (name = "recruitment")
public class Recruitment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column
    private Boolean isActive;
}
