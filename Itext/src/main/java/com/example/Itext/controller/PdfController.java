package com.example.Itext.controller;

import com.example.Itext.models.PdfRequest;
import com.example.Itext.service.GeneratePdfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
public class PdfController {
    private final GeneratePdfService generatePdfService;

    public PdfController(GeneratePdfService generatePdfService) {
        this.generatePdfService = generatePdfService;
    }

    @PostMapping("/api")
    void generatePdf(PdfRequest request) throws IOException {
        generatePdfService.generatePdf(request);
    }
}
