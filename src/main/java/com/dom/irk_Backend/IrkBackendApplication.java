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

}