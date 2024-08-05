package school.faang.user_service.service.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class AmazonS3Service {
    private final AmazonS3 amazonS3;

    public void uploadFile(byte[] file, String bucketName, String fileName, String contentType) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(file);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.length);
        objectMetadata.setContentType(contentType);
        amazonS3.putObject(bucketName, fileName, inputStream, objectMetadata);
    }
}
