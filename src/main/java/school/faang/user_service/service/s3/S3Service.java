package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;

    public void uploadFile(String key, byte[] file, String bucketName) {
        ObjectMetadata metadata = new ObjectMetadata();
        InputStream inputStream = new ByteArrayInputStream(file);
        PutObjectRequest request = new PutObjectRequest(
                bucketName,
                key,
                inputStream,
                metadata
        );
        s3Client.putObject(request);
    }
}
