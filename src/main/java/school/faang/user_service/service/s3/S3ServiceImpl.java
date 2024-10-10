package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    @Value("${services.s3.bucketName}")
    private String bucketName;
    private final AmazonS3 s3Client;

    @Override
    public void uploadFile(InputStream data, ObjectMetadata metadata, String filePath, Long userId) {
        s3Client.putObject(bucketName, filePath, data, metadata);
    }

    @Override
    public InputStream downloadFile(String filePath) {
        return s3Client.getObject(bucketName, filePath).getObjectContent();
    }

    @Override
    public void deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }
}
