package com.example.Itext.service;

import com.example.Itext.models.PdfRequest;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface GeneratePdfService {
    void generatePdf(PdfRequest request) throws IOException;
}
