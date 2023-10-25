package com.example.http.controllers;

import com.example.http.models.RequestDto;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@RestController
@Slf4j
public class PublicController {

    @PostMapping("/api")
    void saveFile(RequestDto requestDto) {
      log.info("file saved");
    }

    @PostConstruct
    void prepare() throws IOException {
        ClassPathResource resource = new ClassPathResource("hello.txt");
        InputStream inputStream = resource.getInputStream();
        var bytes = inputStream.readAllBytes();
        log.info(Arrays.toString(bytes));
    }
}
