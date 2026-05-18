package com.dom.irk_Backend.service;

import com.dom.irk_Backend.model.Course;
import com.dom.irk_Backend.model.Recruitment;
import com.dom.irk_Backend.model.RecruitmentRequest;
import com.dom.irk_Backend.repository.CourseRepository;
import com.dom.irk_Backend.repository.RecruitmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final CourseRepository courseRepository;

    public RecruitmentService(RecruitmentRepository recruitmentRepository, CourseRepository courseRepository) {
        this.recruitmentRepository = recruitmentRepository;
        this.courseRepository = courseRepository;
    }

    public List<Recruitment> getAllRecruitments() {
        return recruitmentRepository.findAll();
    }

    @Transactional
    public Recruitment saveRecruitment(Integer id, RecruitmentRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Błąd: Data zakończenia nie może być wcześniejsza niż data rozpoczęcia!");
        }

        Recruitment recruitment;
        if (id != null) {
            recruitment = recruitmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono rekrutacji"));
        } else {
            recruitment = new Recruitment();
        }

        recruitment.setName(request.getName());
        recruitment.setStartDate(request.getStartDate());
        recruitment.setEndDate(request.getEndDate());
        recruitment.setIsActive(request.getIsActive() != null ? request.getIsActive() : false);

        Recruitment savedRecruitment = recruitmentRepository.save(recruitment);

        // 1. Zdejmujemy tę rekrutację z kierunków
        List<Course> allCourses = courseRepository.findAll();
        for (Course c : allCourses) {
            if (c.getRecruitment() != null && c.getRecruitment().getId().equals(savedRecruitment.getId())) {
                c.setRecruitment(null);
                courseRepository.save(c);
            }
        }

        // 2. Przypisujemy nowy kierunek
        if (request.getCourseId() != null) {
            Course newCourse = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Nie znaleziono kierunku"));
            newCourse.setRecruitment(savedRecruitment);
            courseRepository.save(newCourse);
        }

        return savedRecruitment;
    }

    public void deleteRecruitment(Integer id) {
        recruitmentRepository.deleteById(id);
    }
}