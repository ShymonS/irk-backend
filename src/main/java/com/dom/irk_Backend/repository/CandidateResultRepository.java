package com.dom.irk_Backend.repository;

import com.dom.irk_Backend.model.CandidateResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateResultRepository extends JpaRepository<CandidateResult, Integer>{
}
