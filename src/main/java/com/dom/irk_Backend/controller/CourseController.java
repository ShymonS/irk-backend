package com.dom.irk_Backend.controller;

import com.dom.irk_Backend.model.Course;
import com.dom.irk_Backend.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Integer id, @RequestBody Course updatedCourse) {
        try {
            Course savedCourse = courseService.updateCourse(id, updatedCourse);
            System.out.println("Zaktualizowano kierunek: " + savedCourse.getName());
            return ResponseEntity.ok(savedCourse);

        } catch (RuntimeException e) {
            System.err.println("Błąd aktualizacji: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}