package com.dom.irk_Backend.service;

import com.dom.irk_Backend.model.Course;
import com.dom.irk_Backend.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public void deleteCourse(Integer id) {
        courseRepository.deleteById(id);
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }
}