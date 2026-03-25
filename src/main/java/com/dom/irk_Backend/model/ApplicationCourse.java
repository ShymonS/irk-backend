package com.dom.irk_Backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "application_courses")
public class ApplicationCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private Integer priority;
}