package org.example.springcoinbase.services;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import java.util.List;

@Slf4j
@Service
public class S3Service {


    @Autowired
    S3Client s3Client;

    public void uploadFile(String bucketName, String key, String content) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.putObject(objectRequest, RequestBody.fromBytes(content.getBytes()));
    }

    public List<String> readFile(String bucketName, String key) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
                .key(key)
                .bucket(bucketName)
                .build();
        return s3Client.getObjectAsBytes(objectRequest).asUtf8String().lines().toList();
    }
}
