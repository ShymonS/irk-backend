package com.dom.irk_Backend.controller;

import com.dom.irk_Backend.model.CandidateResult;
import com.dom.irk_Backend.model.FullProfileRequest;
import com.dom.irk_Backend.service.CandidateResultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/results")
public class CandidateResultController {

    private final CandidateResultService resultService;

    public CandidateResultController(CandidateResultService resultService) {
        this.resultService = resultService;
    }
    
    @PostMapping("/full-profile")
    public ResponseEntity<?> saveFullProfile(@RequestBody FullProfileRequest request) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            resultService.saveFullProfile(email, request);

            return ResponseEntity.ok("Profil kandydata został zapisany pomyślnie!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Błąd: " + e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyResults() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            FullProfileRequest myProfile = resultService.getMyProfile(email);
            return ResponseEntity.ok(myProfile);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Błąd: " + e.getMessage());
        }
    }
}