package com.example.Itext.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Configuration {

    @Bean
    public S3Client s3Client(){
        return S3Client.builder()
                .endpointOverride(URI.create("http://127.0.0.1:4566"))
                .region(Region.AP_SOUTHEAST_1)
                .build();
    }
}
