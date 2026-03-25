package com.dom.irk_Backend.repository;

import com.dom.irk_Backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CourseRepository extends JpaRepository<Course, Integer>{
}
