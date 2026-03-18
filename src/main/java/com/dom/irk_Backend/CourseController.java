package com.dom.irk_Backend;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class CourseController {

    @GetMapping("/courses")
    public List<String> getCourses() {
        return List.of("Informatyka", "Matematyka Stosowana", "Zarządzanie i Inżynieria Produkcji");
    }
}