package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

@Component
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Async("threadPool")
    public void uploadToS3(String fileName, byte[] imageBytes, String contentType, String bucketName) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType(contentType);
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(bucketName, fileName, inputStream, metadata);
        s3Client.putObject(putObjectRequest);
    }
}
