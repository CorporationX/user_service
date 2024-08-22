package school.faang.user_service.service;

import school.faang.user_service.config.AwsConfig;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;

    @Autowired
    public S3Service(AwsConfig awsConfig){
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                awsConfig.getAccessKeyId(),
                awsConfig.getSecretKey()
        );

        this.s3Client =  S3Client.builder()
                .region(Region.of(awsConfig.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

        this.bucketName = awsConfig.getBucketName();
    }

    public S3Response uploadFile(Path filePath, String key) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.putObject(putObjectRequest, filePath);
    }

    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }
}