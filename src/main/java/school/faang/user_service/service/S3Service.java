package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exceptions.S3Exception;

import java.io.ByteArrayInputStream;

@RequiredArgsConstructor
@Slf4j
@Service
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    public void uploadFile(byte[] fileContent, String fileName) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileContent.length);
            metadata.setContentType("image/svg+xml");

            PutObjectResult result = amazonS3.putObject(bucketName, fileName, inputStream, metadata);

            log.info("File uploaded to S3: {}, ETag: {}", fileName, result.getETag());
        } catch (Exception e) {
            log.error("Failed to upload file to S3: {}", fileName, e);
            throw new S3Exception("Failed to upload file to S3: " + fileName, e);
        }
    }

    public String generateS3Url(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }
}
