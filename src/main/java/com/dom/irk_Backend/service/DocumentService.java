package com.dom.irk_Backend.service;

import com.dom.irk_Backend.model.Document;
import com.dom.irk_Backend.repository.DocumentRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public void updateDocumentStatus(Integer docId, String newStatus) {
        // Szukamy dokumentu po ID
        Document document = documentRepository.findById(docId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono dokumentu o ID: " + docId));

        // Podmieniamy status i zapisujemy w bazie
        document.setStatus(newStatus);
        documentRepository.save(document);
    }
}