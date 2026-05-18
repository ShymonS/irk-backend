package com.dom.irk_Backend.service;

import com.dom.irk_Backend.model.Candidate;
import com.dom.irk_Backend.model.CandidateResult;
import com.dom.irk_Backend.model.FullProfileRequest;
import com.dom.irk_Backend.repository.CandidateRepository;
import com.dom.irk_Backend.repository.CandidateResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CandidateResultService {

    private final CandidateResultRepository resultRepository;
    private final CandidateRepository candidateRepository;

    public CandidateResultService(CandidateResultRepository resultRepository, CandidateRepository candidateRepository) {
        this.resultRepository = resultRepository;
        this.candidateRepository = candidateRepository;
    }

    public CandidateResult saveResultForCandidate(String candidateEmail, CandidateResult newResult) {
        Candidate candidate = candidateRepository.findByEmail(candidateEmail)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono kandydata o emailu: " + candidateEmail));

        newResult.setCandidate(candidate);
        return resultRepository.save(newResult);
    }

    @Transactional
    public void saveFullProfile(String email, FullProfileRequest request) {
        Candidate candidate = candidateRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono kandydata"));

        // Czyścimy poprzednie wyniki (Wipe & Replace)
        resultRepository.deleteByCandidate(candidate);

        // 1. Zapisujemy matury podstawowe
        if (request.getMandatory() != null) {
            request.getMandatory().forEach((subject, score) -> {
                if (score != null) {
                    validateExamScore(subject, score); // WALIDACJA
                    saveSingleResult(candidate, subject + " (Podstawa)", score);
                }
            });
        }

        // 2. Zapisujemy matury rozszerzone
        if (request.getExtended() != null) {
            request.getExtended().forEach(item -> {
                if (item.getSubject() != null && !item.getSubject().isEmpty() && item.getValue() != null) {
                    validateExamScore(item.getSubject(), item.getValue()); // WALIDACJA
                    saveSingleResult(candidate, item.getSubject() + " (Rozszerzenie)", item.getValue());
                }
            });
        }

        // 3. Zapisujemy oceny ze świadectwa
        if (request.getGrades() != null) {
            request.getGrades().forEach(item -> {
                if (item.getSubject() != null && item.getValue() != null) {
                    validateSchoolGrade(item.getSubject(), item.getValue()); // WALIDACJA
                    saveSingleResult(candidate, "Ocena końcowa: " + item.getSubject(), item.getValue());
                }
            });
        }
    }

    private void saveSingleResult(Candidate candidate, String subject, Integer score) {
        CandidateResult result = new CandidateResult();
        result.setCandidate(candidate);
        result.setSubjectName(subject);
        result.setScore(score);
        resultRepository.save(result);
    }

    // --- METODY WALIDUJĄCE ---

    private void validateExamScore(String subject, Integer score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException(
                    "Nieprawidłowy wynik! Matura z przedmiotu '" + subject + "' musi być w przedziale 0-100. Przesłano: " + score
            );
        }
    }

    private void validateSchoolGrade(String subject, Integer grade) {
        // Zakładam skalę 2-6 (możesz zmienić na 1-6 jeśli przyjmujecie jedynki)
        if (grade < 2 || grade > 6) {
            throw new IllegalArgumentException(
                    "Nieprawidłowa ocena! Ocena ze świadectwa z przedmiotu '" + subject + "' musi być w przedziale 2-6. Przesłano: " + grade
            );
        }
    }

    public FullProfileRequest getMyProfile(String email) {
        Candidate candidate = candidateRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono kandydata"));

        List<CandidateResult> rawResults = resultRepository.findByCandidateId(candidate.getId());

        FullProfileRequest response = new FullProfileRequest();
        Map<String, Integer> mandatory = new HashMap<>();
        List<FullProfileRequest.SubjectScore> extended = new ArrayList<>();
        List<FullProfileRequest.SubjectScore> grades = new ArrayList<>();

        for (CandidateResult r : rawResults) {
            String name = r.getSubjectName();

            if (name.endsWith("(Podstawa)")) {
                mandatory.put(name.replace(" (Podstawa)", ""), r.getScore());

            } else if (name.endsWith("(Rozszerzenie)")) {
                FullProfileRequest.SubjectScore ss = new FullProfileRequest.SubjectScore();
                ss.setSubject(name.replace(" (Rozszerzenie)", ""));
                ss.setValue(r.getScore());
                extended.add(ss);

            } else if (name.startsWith("Ocena końcowa: ")) {
                FullProfileRequest.SubjectScore ss = new FullProfileRequest.SubjectScore();
                ss.setSubject(name.replace("Ocena końcowa: ", ""));
                ss.setValue(r.getScore());
                grades.add(ss);
            }
        }

        response.setMandatory(mandatory);
        response.setExtended(extended);
        response.setGrades(grades);

        return response;
    }
}