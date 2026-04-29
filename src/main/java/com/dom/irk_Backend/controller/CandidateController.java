package com.dom.irk_Backend.controller;

import com.dom.irk_Backend.model.Candidate;
import com.dom.irk_Backend.model.LoginRequest;
import com.dom.irk_Backend.service.CandidateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;

@RestController
@CrossOrigin
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService){
        this.candidateService = candidateService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Candidate candidate) {

        if (candidateService.emailExists(candidate.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Rejestracja nieudana - email zajęty");
        }

        Candidate savedCandidate = candidateService.registerCandidate(candidate);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCandidate);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request){
        System.out.println("Próba logowania dla: " + loginRequest.getEmail());

        Candidate loggedInCandidate = candidateService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        if (loggedInCandidate != null){
            System.out.println("Sukces! Zalogowano pomyślnie.");

            UsernamePasswordAuthenticationToken authReq =
                    new UsernamePasswordAuthenticationToken(loggedInCandidate.getEmail(), null, new ArrayList<>());

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authReq);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

            return ResponseEntity.ok(loggedInCandidate);
        } else {
            System.out.println("Błąd logowania, złe dane.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Błędny email lub hasło");
        }
    }
}
