package com.dom.irk_Backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table (name = "candidate_results")
public class CandidateResult {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column (nullable = false)
    private String subjectName;

    @Column (nullable = false)
    private Integer score;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;
}
