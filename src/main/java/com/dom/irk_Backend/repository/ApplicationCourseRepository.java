package com.dom.irk_Backend.repository;

import com.dom.irk_Backend.model.ApplicationCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationCourseRepository extends JpaRepository<ApplicationCourse, Integer> {
}