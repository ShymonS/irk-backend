package com.dom.irk_Backend.service;

import com.dom.irk_Backend.model.Course;
import com.dom.irk_Backend.repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course updateCourse(Integer id, Course updatedData) {
        return courseRepository.findById(id).map(existingCourse -> {

            existingCourse.setName(updatedData.getName());
            existingCourse.setPlacesLimit(updatedData.getPlacesLimit());

            return courseRepository.save(existingCourse);

        }).orElseThrow(() -> new RuntimeException("Nie znaleziono kierunku o ID: " + id));
    }
}