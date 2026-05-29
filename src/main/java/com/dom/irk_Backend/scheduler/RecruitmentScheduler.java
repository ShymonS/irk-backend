package com.dom.irk_Backend.scheduler;

import com.dom.irk_Backend.model.Recruitment;
import com.dom.irk_Backend.repository.RecruitmentRepository;
import com.dom.irk_Backend.service.ApplicationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class RecruitmentScheduler {

    private final RecruitmentRepository recruitmentRepository;
    private final ApplicationService applicationService;

    public RecruitmentScheduler(RecruitmentRepository recruitmentRepository, ApplicationService applicationService) {
        this.recruitmentRepository = recruitmentRepository;
        this.applicationService = applicationService;
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void processEndedRecruitmentsAutomatically() {
        System.out.println("Uruchamiam nocny algorytm zamykania rekrutacji...");

        LocalDate today = LocalDate.now();

        // Szukamy rekrutacji, które powinny się już zamknąć (endDate było wczoraj lub wcześniej) i są wciąż IsActive=true
        List<Recruitment> recruitmentsToProcess = recruitmentRepository.findByEndDateBeforeAndIsActiveTrue(today);

        for (Recruitment rec : recruitmentsToProcess) {
            try {
                applicationService.processRecruitmentResults(rec.getId());
                System.out.println("Sukces: Zamknięto i przeliczono wyniki dla rekrutacji ID: " + rec.getId());
            } catch (Exception e) {
                System.err.println("Błąd podczas przeliczania rekrutacji ID: " + rec.getId() + " - " + e.getMessage());
            }
        }
    }
}