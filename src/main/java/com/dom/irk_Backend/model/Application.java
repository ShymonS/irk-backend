package com.dom.irk_Backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table (name = "applications")
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @ManyToOne
    @JoinColumn(name = "recruitment_id", nullable = false)
    private Recruitment recruitment;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
