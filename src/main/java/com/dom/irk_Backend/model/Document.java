package com.dom.irk_Backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table (name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String documentName;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn (name = "application_id", nullable = false)
    private Application application;
}
