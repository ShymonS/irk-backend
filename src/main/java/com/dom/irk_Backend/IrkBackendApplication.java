package com.dom.irk_Backend;

import com.dom.irk_Backend.model.Candidate;
import com.dom.irk_Backend.repository.CandidateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IrkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(IrkBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(CandidateRepository candidateRepository) {
        return args -> {
            if (candidateRepository.count() == 0) {
                Candidate candidateTestowy = new Candidate();
                candidateTestowy.setFirstName("Jan");
                candidateTestowy.setLastName("Kowalski");
                candidateTestowy.setEmail("testowy@mail.com");
                candidateTestowy.setPhoneNumber("213253132");

                candidateRepository.save(candidateTestowy);

                System.out.println("Zapisano pierwszego kandydata do bazy");
            } else {
                System.out.println("Baza ma już dane, nie testuje.");
            }
        };
    }
}