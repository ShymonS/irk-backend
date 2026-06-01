package com.dom.irk_Backend.controller;

import com.dom.irk_Backend.model.Recruitment;
import com.dom.irk_Backend.model.RecruitmentRequest;
import com.dom.irk_Backend.service.RecruitmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/recruitments")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    public RecruitmentController(RecruitmentService recruitmentService) {
        this.recruitmentService = recruitmentService;
    }

    @GetMapping
    public List<Recruitment> getAll() {
        return recruitmentService.getAllRecruitments();
    }

    @GetMapping("/active")
    public ResponseEntity<List<Recruitment>> getActiveRecruitments() {
        // Pobieramy wszystkie i filtrujemy tylko aktywne (isActive = true)
        return ResponseEntity.ok(recruitmentService.getActiveRecruitments());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RecruitmentRequest request) {
        try {
            return ResponseEntity.ok(recruitmentService.saveRecruitment(null, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody RecruitmentRequest request) {
        try {
            return ResponseEntity.ok(recruitmentService.saveRecruitment(id, request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        recruitmentService.deleteRecruitment(id);
        return ResponseEntity.ok().build();
    }
}
