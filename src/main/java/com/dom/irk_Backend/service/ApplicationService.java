package com.dom.irk_Backend.service;

import com.dom.irk_Backend.model.*;
import com.dom.irk_Backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void processRecruitmentResults(Integer recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono rekrutacji."));

        Course course = recruitment.getCourse();
        if (course == null || course.getPlacesLimit() == null) {
            throw new IllegalStateException("Rekrutacja nie ma przypisanego kierunku lub limitu miejsc.");
        }

        int limit = course.getPlacesLimit();

        // 1. Pobieramy wszystkie aplikacje dla tej rekrutacji
        List<Application> applications = applicationRepository.findByRecruitmentId(recruitmentId);

        if (!applications.isEmpty()) {
            // 2. Sortujemy kandydatów malejąco po punktach
            applications.sort((a1, a2) -> Integer.compare(a2.getPoints(), a1.getPoints()));

            // 3. Rozdajemy statusy na podstawie limitu miejsc
            for (int i = 0; i < applications.size(); i++) {
                Application app = applications.get(i);

                // Jeśli indeks kandydata jest mniejszy niż limit -> Zakwalifikowany
                if (i < limit) {
                    app.setStatus("ZAKWALIFIKOWANY");
                } else {
                    app.setStatus("LISTA REZERWOWA");
                }
            }
            // Zapisujemy zaktualizowane statusy kandydatów
            applicationRepository.saveAll(applications);
        }

        // 4. Wyłączamy rekrutację, żeby zniknęła z otwartych i nie przeliczała się ponownie
        recruitment.setIsActive(false);
        recruitmentRepository.save(recruitment);
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