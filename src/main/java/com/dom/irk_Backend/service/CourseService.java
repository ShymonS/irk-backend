package com.dom.irk_Backend.service;

import com.dom.irk_Backend.model.Course;
import com.dom.irk_Backend.model.CourseRequirement;
import com.dom.irk_Backend.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course createCourse(Course course) {
        validateCourse(course, null);
        if (course.getRequirements() != null) {
            for (CourseRequirement req : course.getRequirements()) {
                req.setCourse(course);
            }
        }
        return courseRepository.save(course);
    }

    public Course updateCourse(Integer id, Course updatedData) {
        validateCourse(updatedData, id);

        return courseRepository.findById(id).map(existingCourse -> {
            existingCourse.setName(updatedData.getName());
            existingCourse.setPlacesLimit(updatedData.getPlacesLimit());
            if (existingCourse.getRequirements() != null) {
                existingCourse.getRequirements().clear();
            }

            if (updatedData.getRequirements() != null) {
                for (CourseRequirement req : updatedData.getRequirements()) {
                    req.setId(null);
                    req.setCourse(existingCourse);
                    existingCourse.getRequirements().add(req);
                }
            }
            return courseRepository.save(existingCourse);
        }).orElseThrow(() -> new RuntimeException("Nie znaleziono kierunku o ID: " + id));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public void deleteCourse(Integer id) {
        courseRepository.deleteById(id);
    }

    private void validateCourse(Course course, Integer currentId) {
        String name = course.getName();

        // Zabezpieczenie przed pustymi danymi
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Błąd: Nazwa kierunku nie może być pusta.");
        }

        // --- WALIDACJA 1: Liczby w nazwie ---
        if (name.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Błąd: Nazwa kierunku nie może zawierać cyfr.");
        }

        // --- WALIDACJA 2: Limit miejsc > 0 ---
        if (course.getPlacesLimit() == null || course.getPlacesLimit() <= 0) {
            throw new IllegalArgumentException("Błąd: Limit miejsc musi być większy od 0.");
        }

        // --- WALIDACJA 3: Unikalność nazwy (niezależnie od wielkości liter) ---
        Optional<Course> existingCourse = courseRepository.findByNameIgnoreCase(name);
        if (existingCourse.isPresent()) {
            if (currentId != null && existingCourse.get().getId().equals(currentId)) {
                return;
            }
            throw new IllegalArgumentException("Błąd: Kierunek o nazwie '" + name + "' już istnieje.");
        }
    }
}