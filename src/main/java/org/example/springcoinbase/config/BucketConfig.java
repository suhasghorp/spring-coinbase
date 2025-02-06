package org.example.springcoinbase.config;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class BucketConfig {

    @Value("${AWS_ACCESS_KEY}")
    String awsAccessKey;

    @Value("${AWS_SECRET_KEY}")
    String awsSecretKey;

    @Bean
    public S3Client getAmazonS3Client() {
        AwsCredentials creds = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
        AwsCredentialsProvider provider = StaticCredentialsProvider.create(creds);
        return S3Client.builder().credentialsProvider(provider).region(Region.of("us-east-1")).build();
    }
}