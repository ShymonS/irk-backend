package com.dom.irk_Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column
    private Integer points;

    @Column
    private Integer priority;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("application")
    private List<Document> documents;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
