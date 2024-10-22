package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service {
    @Value("${services.s3.bucketName}")
    private String bucketName;
    private final AmazonS3 s3Client;

    @Override
    public void uploadFile(byte[] data, String filePath, Long userId, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        metadata.setContentType(contentType);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        s3Client.putObject(bucketName, filePath, byteArrayInputStream, metadata);
    }

    @Override
    public byte[] downloadFile(String filePath) throws IOException {
        return s3Client.getObject(bucketName, filePath).getObjectContent().readAllBytes();
    }

    @Override
    public void deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
    }
}
