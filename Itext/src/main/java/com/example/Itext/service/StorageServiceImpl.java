package com.example.Itext.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {
    private final S3Client s3Client;
    private static final String bucket = "logo-bucket";

    public StorageServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public void putFile(String key, byte[] bytes) {
        log.info("Saving to s3 bucket: {}", key);
        PutObjectRequest putObjectRequest = PutObjectRequest
                .builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

    }

    @Override
    public byte[] getGetFile(String key) {
        log.info("Getting from s3 bucket: {}", key);

        GetObjectRequest objectRequest = GetObjectRequest
                .builder()
                .key(key)
                .bucket(bucket)
                .build();

        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(objectRequest);

        return objectBytes.asByteArray();
    }

}
