package com.dom.irk_Backend.controller;

import com.dom.irk_Backend.model.Administrator;
import com.dom.irk_Backend.model.LoginRequest;
import com.dom.irk_Backend.service.AdministratorService;
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
@RequestMapping("/api/admins")
public class AdministratorController {

    private final AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService){
        this.administratorService = administratorService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request){
        System.out.println("Próba logowania do Panelu Admina dla: " + loginRequest.getEmail());

        Administrator loggedInAdmin = administratorService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        if (loggedInAdmin != null){
            System.out.println("Sukces! Administrator zalogowany pomyślnie.");

            UsernamePasswordAuthenticationToken authReq =
                    new UsernamePasswordAuthenticationToken(loggedInAdmin.getEmail(), null, new ArrayList<>());

            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authReq);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

            return ResponseEntity.ok(loggedInAdmin);
        } else {
            System.out.println("Błąd logowania do panelu administratora (złe dane).");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Błędny email lub hasło administratora");
        }
    }
}