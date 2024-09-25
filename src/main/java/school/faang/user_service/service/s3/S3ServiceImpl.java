package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
public class S3ServiceImpl implements S3Service {

    private final String bucketName;
    private final AmazonS3 s3Client;

    @Autowired
    public S3ServiceImpl(@Value("${services.s3.bucketName}") String bucketName, AmazonS3 s3Client) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public String uploadFile(String fileName, InputStream data, ObjectMetadata metadata, Long userId) {
        String fileKey = String.format("user_%d/%s", userId, fileName);
        PutObjectRequest request = new PutObjectRequest(bucketName, fileKey, data, metadata);
        return s3Client.putObject(request).getContentMd5();
    }

    @Override
    public InputStream downloadFile(String bucketName, String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String deleteFile(String bucketName, String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
