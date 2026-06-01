package com.dom.irk_Backend.repository;

import com.dom.irk_Backend.model.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Integer>{
    List<Recruitment> findByEndDateBeforeAndIsActiveTrue(LocalDate date);
    List<Recruitment> findByIsActiveTrueAndEndDateGreaterThanEqual(LocalDate today);
}
