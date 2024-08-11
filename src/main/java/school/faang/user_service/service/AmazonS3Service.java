package school.faang.user_service.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.AmazonCredentials;

import java.io.ByteArrayInputStream;

import static org.apache.http.entity.ContentType.IMAGE_JPEG;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {

    private final AmazonS3 amazonS3;
    private final AmazonCredentials amazonCredentials;

    public String uploadFile(String key, byte[] file) {

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.length);
        objectMetadata.setContentType(IMAGE_JPEG.getMimeType());
        PutObjectRequest putObjectRequest =
                new PutObjectRequest(
                        amazonCredentials.getBucketName(),
                        key,
                        new ByteArrayInputStream(file),
                        objectMetadata
                );
        amazonS3.putObject(putObjectRequest);
        return key;
    }
}
