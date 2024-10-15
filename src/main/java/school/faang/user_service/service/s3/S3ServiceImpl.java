package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.FileOperationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
@Getter
public class S3ServiceImpl implements S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public void uploadFile(ByteArrayOutputStream outputStream, String key) {
        log.debug("Starting file upload to S3 with key: {}", key);
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(outputStream.size());
            objectMetadata.setContentType(MediaType.IMAGE_JPEG.getType());
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName, key, new ByteArrayInputStream(outputStream.toByteArray()), objectMetadata);
            s3Client.putObject(putObjectRequest);
            log.info("File uploaded successfully to S3 with key: {}", key);
        } catch (Exception e) {
            throw new FileOperationException("Failed to upload file to S3 with key: {}".formatted(key), e);
        }
    }

    @Override
    public void uploadFile(String key, byte[] file, String bucketName, long contentLength, MediaType contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        metadata.setContentType(contentType.toString());
        InputStream inputStream = new ByteArrayInputStream(file);
        PutObjectRequest request = new PutObjectRequest(
                bucketName,
                key,
                inputStream,
                metadata
        );
        s3Client.putObject(request);
    }

    @Override
    public void deleteFile(String key) {
        log.debug("Starting file deletion from S3 with key: {}", key);
        s3Client.deleteObject(bucketName, key);
    }

    @Override
    public InputStream downloadFile(String key) {
        log.debug("Starting file download from S3 with key: {}", key);
        return s3Client.getObject(bucketName, key).getObjectContent();
    }
}
