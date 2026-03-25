package com.dom.irk_Backend.controller;

import com.dom.irk_Backend.model.Candidate;
import com.dom.irk_Backend.service.CandidateService;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService){
        this.candidateService = candidateService;
    }

    @PostMapping("/register")
    public Candidate register(@RequestBody Candidate candidate){
        System.out.println("Połączenie z Reactem nawiązane! Dowód - Imię kandydata: " + candidate.getFirstName());

        return candidateService.registerCandidate(candidate);
    }
}
