package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exceptions.S3Exception;


import java.io.ByteArrayInputStream;

@Service
@Slf4j
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadFile(byte[] fileContent, String fileName) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileContent);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileContent.length);
            metadata.setContentType("image/svg+xml");

            amazonS3.putObject(bucketName, fileName, inputStream, metadata);

            log.info("File uploaded to S3: {}", fileName);
            return amazonS3.getUrl(bucketName, fileName).toString();
        } catch (Exception e) {
            log.error("Failed to upload file to S3: {}", fileName, e);
            throw new S3Exception("Failed to upload file to S3: " + fileName, e);
        }
    }
}

