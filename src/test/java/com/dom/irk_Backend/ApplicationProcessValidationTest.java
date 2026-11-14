package com.dom.irk_Backend;

import com.dom.irk_Backend.model.Application;
import com.dom.irk_Backend.model.Candidate;
import com.dom.irk_Backend.model.CandidateResult;
import com.dom.irk_Backend.model.Course;
import com.dom.irk_Backend.model.Recruitment;
import com.dom.irk_Backend.repository.ApplicationRepository;
import com.dom.irk_Backend.repository.CandidateRepository;
import com.dom.irk_Backend.repository.CandidateResultRepository;
import com.dom.irk_Backend.repository.CourseRepository;
import com.dom.irk_Backend.repository.RecruitmentRepository;
import com.dom.irk_Backend.service.ApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Testy walidacji procesu składania aplikacji.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(ApplicationService.class)
class ApplicationProcessValidationTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CandidateResultRepository candidateResultRepository;

    @Autowired
    private ApplicationService applicationService;

    private Candidate candidate;
    private Course course;

    @BeforeEach
    void setUp() {
        applicationRepository.deleteAll();
        candidateResultRepository.deleteAll();
        candidateRepository.deleteAll();
        recruitmentRepository.deleteAll();
        courseRepository.deleteAll();

        candidate = new Candidate();
        candidate.setFirstName("Jan");
        candidate.setLastName("Kowalski");
        candidate.setEmail("jan.kowalski@example.com");
        candidate.setPhoneNumber("123456789");
        candidate.setPasswordHash("hashed");
        candidate = candidateRepository.save(candidate);

        course = new Course();
        course.setName("Informatyka");
        course.setPlacesLimit(10);
        course = courseRepository.save(course);

        CandidateResult result = new CandidateResult();
        result.setSubjectName("Matematyka (Podstawa)");
        result.setScore(80);
        result.setCandidate(candidate);
        candidateResultRepository.save(result);
    }

    private Recruitment createRecruitment(String name, LocalDate startDate, LocalDate endDate, boolean active) {
        Recruitment recruitment = new Recruitment();
        recruitment.setName(name);
        recruitment.setStartDate(startDate);
        recruitment.setEndDate(endDate);
        recruitment.setIsActive(active);
        recruitment.setCourse(course);
        return recruitmentRepository.save(recruitment);
    }

    // ==================== SCRUM-20: aplikowanie po upływie terminu rekrutacji ====================

    @Test
    @DisplayName("SCRUM-20: Nie powinno być możliwe złożenie aplikacji po upływie terminu rekrutacji")
    void shouldNotAllowApplicationAfterRecruitmentEndDate() {
        Recruitment recruitment = createRecruitment(
                "Rekrutacja po terminie",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(1),
                true
        );

        assertThatThrownBy(() -> applicationService.applyForRecruitment(candidate.getEmail(), recruitment.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Rekrutacja nie odbywa się w obecnym terminie.");

        assertThat(applicationRepository.count()).isZero();
    }

    @Test
    @DisplayName("SCRUM-20: Powinno być możliwe złożenie aplikacji w ostatnim dniu terminu rekrutacji")
    void shouldAllowApplicationOnLastDayOfRecruitment() {
        Recruitment recruitment = createRecruitment(
                "Rekrutacja w ostatnim dniu",
                LocalDate.now().minusDays(5),
                LocalDate.now(),
                true
        );

        Application application = applicationService.applyForRecruitment(candidate.getEmail(), recruitment.getId());

        assertThat(application.getStatus()).isEqualTo("ZŁOŻONA");
        assertThat(applicationRepository.count()).isEqualTo(1);
    }

    // ==================== SCRUM-21: aplikowanie na zamkniętą rekrutację ====================

    @Test
    @DisplayName("SCRUM-21: Nie powinno być możliwe złożenie aplikacji na zamkniętą rekrutację (isActive = false)")
    void shouldNotAllowApplicationToInactiveRecruitment() {
        Recruitment recruitment = createRecruitment(
                "Rekrutacja zamknięta",
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(10),
                false
        );

        assertThatThrownBy(() -> applicationService.applyForRecruitment(candidate.getEmail(), recruitment.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ta rekrutacja jest obecnie zamknięta.");

        assertThat(applicationRepository.count()).isZero();
    }

    @Test
    @DisplayName("SCRUM-21: Powinno być możliwe złożenie aplikacji na aktywną rekrutację (isActive = true)")
    void shouldAllowApplicationToActiveRecruitment() {
        Recruitment recruitment = createRecruitment(
                "Rekrutacja aktywna",
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(5),
                true
        );

        Application application = applicationService.applyForRecruitment(candidate.getEmail(), recruitment.getId());

        assertThat(application.getStatus()).isEqualTo("ZŁOŻONA");
        assertThat(applicationRepository.count()).isEqualTo(1);
    }

    // ==================== SCRUM-22: warunki brzegowe danych rekrutacji ====================

    @Test
    @DisplayName("SCRUM-22: Powinno być możliwe złożenie aplikacji w dniu rozpoczęcia rekrutacji")
    void shouldAllowApplicationOnStartDate() {
        Recruitment recruitment = createRecruitment(
                "Rekrutacja start dziś",
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                true
        );

        Application application = applicationService.applyForRecruitment(candidate.getEmail(), recruitment.getId());

        assertThat(application.getStatus()).isEqualTo("ZŁOŻONA");
        assertThat(applicationRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("SCRUM-22: Nie powinno być możliwe złożenie aplikacji przed rozpoczęciem rekrutacji")
    void shouldNotAllowApplicationBeforeStartDate() {
        Recruitment recruitment = createRecruitment(
                "Rekrutacja start jutro",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(10),
                true
        );

        assertThatThrownBy(() -> applicationService.applyForRecruitment(candidate.getEmail(), recruitment.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Rekrutacja nie odbywa się w obecnym terminie.");

        assertThat(applicationRepository.count()).isZero();
    }

    @Test
    @DisplayName("SCRUM-22: Powinno być możliwe złożenie aplikacji gdy data startu i końca to ten sam dzień")
    void shouldAllowApplicationWhenStartEqualsEndDate() {
        Recruitment recruitment = createRecruitment(
                "Rekrutacja jednodniowa",
                LocalDate.now(),
                LocalDate.now(),
                true
        );

        Application application = applicationService.applyForRecruitment(candidate.getEmail(), recruitment.getId());

        assertThat(application.getStatus()).isEqualTo("ZŁOŻONA");
        assertThat(applicationRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("SCRUM-22: Nie powinno być możliwe złożenie aplikacji dzień po zakończeniu rekrutacji")
    void shouldNotAllowApplicationOneDayAfterEndDate() {
        Recruitment recruitment = createRecruitment(
                "Rekrutacja zakończona wczoraj",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(1),
                true
        );

        assertThatThrownBy(() -> applicationService.applyForRecruitment(candidate.getEmail(), recruitment.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Rekrutacja nie odbywa się w obecnym terminie.");

        assertThat(applicationRepository.count()).isZero();
    }

    // ==================== SCRUM-23: integralność zapisu po nieudanej próbie aplikowania ====================

    @Test
    @DisplayName("SCRUM-23: Nie powinien zostać zapisany rekord aplikacji po nieudanej próbie (zamknięta rekrutacja)")
    void shouldNotPersistApplicationWhenRecruitmentIsClosed() {
        Recruitment recruitment = createRecruitment(
                "Rekrutacja zamknięta",
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(10),
                false
        );

        long countBefore = applicationRepository.count();

        assertThatThrownBy(() -> applicationService.applyForRecruitment(candidate.getEmail(), recruitment.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Ta rekrutacja jest obecnie zamknięta.");

        assertThat(applicationRepository.count()).isEqualTo(countBefore);
    }

    @Test
    @DisplayName("SCRUM-23: Nie powinien zostać zapisany rekord aplikacji po nieudanej próbie (po terminie rekrutacji)")
    void shouldNotPersistApplicationAfterRecruitmentEnded() {
        Recruitment recruitment = createRecruitment(
                "Rekrutacja po terminie",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(1),
                true
        );

        long countBefore = applicationRepository.count();

        assertThatThrownBy(() -> applicationService.applyForRecruitment(candidate.getEmail(), recruitment.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Rekrutacja nie odbywa się w obecnym terminie.");

        assertThat(applicationRepository.count()).isEqualTo(countBefore);
    }

    @Test
    @DisplayName("SCRUM-23: Nie powinien zostać utworzony duplikat aplikacji dla tego samego kandydata i rekrutacji")
    void shouldNotPersistDuplicateApplication() {
        Recruitment recruitment = createRecruitment(
                "Rekrutacja otwarta",
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(5),
                true
        );

        applicationService.applyForRecruitment(candidate.getEmail(), recruitment.getId());
        long countAfterFirst = applicationRepository.count();
        assertThat(countAfterFirst).isEqualTo(1);

        assertThatThrownBy(() -> applicationService.applyForRecruitment(candidate.getEmail(), recruitment.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Już złożyłeś aplikację w tej rekrutacji!");

        assertThat(applicationRepository.count()).isEqualTo(countAfterFirst);
    }

    @Test
    @DisplayName("SCRUM-23: Powinien zostać zapisany rekord aplikacji przy poprawnej aplikacji")
    void shouldPersistApplicationWhenValid() {
        Recruitment recruitment = createRecruitment(
                "Rekrutacja otwarta",
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(5),
                true
        );

        long countBefore = applicationRepository.count();

        Application application = applicationService.applyForRecruitment(candidate.getEmail(), recruitment.getId());

        assertThat(application.getId()).isNotNull();
        assertThat(application.getStatus()).isEqualTo("ZŁOŻONA");
        assertThat(application.getCandidate().getId()).isEqualTo(candidate.getId());
        assertThat(application.getRecruitment().getId()).isEqualTo(recruitment.getId());
        assertThat(applicationRepository.count()).isEqualTo(countBefore + 1);
    }
}