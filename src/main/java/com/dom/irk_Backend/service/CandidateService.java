package com.dom.irk_Backend.service;

import com.dom.irk_Backend.model.Candidate;
import com.dom.irk_Backend.repository.CandidateRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;

    public CandidateService(CandidateRepository candidateRepository, PasswordEncoder passwordEncoder){
        this.candidateRepository = candidateRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Candidate registerCandidate(Candidate newCandidate){
        // 1. Uruchamiamy walidację
        validateCandidate(newCandidate);

        // 2. Jeśli przeszło, szyfrujemy hasło i zapisujemy
        String rawPassword = newCandidate.getPasswordHash();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        newCandidate.setPasswordHash(encodedPassword);

        return candidateRepository.save(newCandidate);
    }

    // --- METODA WALIDUJĄCA KANDYDATA ---
    private void validateCandidate(Candidate candidate) {
        // Sprawdzenie czy pola nie są puste
        if (candidate.getFirstName() == null || candidate.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Imię nie może być puste.");
        }
        if (candidate.getLastName() == null || candidate.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nazwisko nie może być puste.");
        }
        if (candidate.getPasswordHash() == null || candidate.getPasswordHash().length() < 6) {
            throw new IllegalArgumentException("Hasło musi mieć co najmniej 6 znaków.");
        }

        // Sprawdzenie formatu email
        if (candidate.getEmail() == null || !candidate.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Niepoprawny format adresu email.");
        }

        // Sprawdzenie unikalności emaila
        if (emailExists(candidate.getEmail())) {
            // Zwracamy specjalny wyjątek, który kontroler wyłapie, by zwrócić błąd 409!
            throw new IllegalStateException("Email jest już zajęty.");
        }
    }

    public Candidate authenticate(String email, String password){
        Optional<Candidate> candidateOptional = candidateRepository.findByEmail(email);

        if(candidateOptional.isPresent()){
            Candidate candidate = candidateOptional.get();

            if(passwordEncoder.matches(password, candidate.getPasswordHash())){
                return candidate;
            }
        }
        return null;
    }

    public boolean emailExists(String email) {
        return candidateRepository.findByEmail(email).isPresent();
    }
}