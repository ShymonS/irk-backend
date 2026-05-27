package com.dom.irk_Backend.service;

import com.dom.irk_Backend.model.*;
import com.dom.irk_Backend.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CandidateRepository candidateRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final CandidateResultRepository resultRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              CandidateRepository candidateRepository,
                              RecruitmentRepository recruitmentRepository,
                              CandidateResultRepository resultRepository) {
        this.applicationRepository = applicationRepository;
        this.candidateRepository = candidateRepository;
        this.recruitmentRepository = recruitmentRepository;
        this.resultRepository = resultRepository;
    }

    public Application applyForRecruitment(String candidateEmail, Integer recruitmentId) {
        Candidate candidate = candidateRepository.findByEmail(candidateEmail)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono kandydata."));

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono rekrutacji."));

        // 1. Walidacja: Czy rekrutacja jest w ogóle aktywna
        if (!recruitment.getIsActive()) {
            throw new IllegalStateException("Ta rekrutacja jest obecnie zamknięta.");
        }

        LocalDate today = LocalDate.now();
        if (today.isBefore(recruitment.getStartDate()) || today.isAfter(recruitment.getEndDate())) {
            throw new IllegalStateException("Rekrutacja nie odbywa się w obecnym terminie.");
        }

        // 2. Walidacja: Czy kandydat już tu aplikował
        if (applicationRepository.existsByCandidateAndRecruitment(candidate, recruitment)) {
            throw new IllegalStateException("Już złożyłeś aplikację w tej rekrutacji!");
        }

        // 3. LICZENIE PUNKTÓW
        List<CandidateResult> results = resultRepository.findByCandidateId(candidate.getId());
        if (results == null || results.isEmpty()) {
            throw new IllegalStateException("Musisz najpierw uzupełnić swoje wyniki egzaminów maturalnych w profilu!");
        }

        double totalBasePoints = 0.0;
        double maxExtendedPoints = 0.0;

        for (CandidateResult res : results) {
            // A. Sumujemy wszystkie matury podstawowe
            if (res.getSubjectName().endsWith("(Podstawa)")) {
                totalBasePoints += (res.getScore() * 0.5);
            }
        }

        // B. Wybieramy najlepsze rozszerzenie według mnożników kierunku
        Course course = recruitment.getCourse();
        if (course != null && course.getRequirements() != null) {
            for (CourseRequirement req : course.getRequirements()) {

                // Szukamy w wynikach kandydata przedmiotu z dopiskiem "(Rozszerzenie)"
                String expectedSubjectName = req.getSubjectName() + " (Rozszerzenie)";

                for (CandidateResult res : results) {
                    if (res.getSubjectName().equals(expectedSubjectName)) {

                        double calculatedPoints = res.getScore() * req.getMultiplier();

                        // Zapisujemy, jeśli jest to najlepszy wynik z rozszerzenia do tej pory
                        if (calculatedPoints > maxExtendedPoints) {
                            maxExtendedPoints = calculatedPoints;
                        }
                    }
                }
            }
        }

        int finalScore = (int) Math.round(totalBasePoints + maxExtendedPoints);

        // 4. Tworzymy aplikację i zapisujemy do bazy
        Application application = new Application();
        application.setCandidate(candidate);
        application.setRecruitment(recruitment);
        application.setStatus("ZŁOŻONA");
        application.setPoints(finalScore);

        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsForRecruitment(Integer recruitmentId) {
        return applicationRepository.findByRecruitmentId(recruitmentId);
    }

    public List<Application> getMyApplications(String candidateEmail) {
        Candidate candidate = candidateRepository.findByEmail(candidateEmail)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono kandydata."));
        return applicationRepository.findByCandidate(candidate);
    }
}