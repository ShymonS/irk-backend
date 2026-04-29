package com.dom.irk_Backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (nullable = false)
    private String name;

    @Column (nullable = false)
    private Integer placesLimit;

    // Klucz obcy
    @ManyToOne
    @JoinColumn(name = "recruitment_id", nullable = true)
    private Recruitment recruitment;
}
