package com.dom.irk_Backend.controller;

import com.dom.irk_Backend.model.DocumentStatusRequest;
import com.dom.irk_Backend.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateDocumentStatus(
            @PathVariable Integer id,
            @RequestBody DocumentStatusRequest request) {

        try {
            documentService.updateDocumentStatus(id, request.getStatus());
            return ResponseEntity.ok().body("Status dokumentu zaktualizowany.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}