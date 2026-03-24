package com.dom.irk_Backend.repository;

import com.dom.irk_Backend.model.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Integer>{
}
