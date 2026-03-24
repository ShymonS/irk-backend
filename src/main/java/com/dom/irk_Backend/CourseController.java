package com.dom.irk_Backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CourseController {

    @GetMapping("/test")
    public String dziala() {
        return "Test aplikacji";
    }
}