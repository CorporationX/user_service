package school.faang.user_service.service.s3;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.s3.S3Config;
import school.faang.user_service.exception.FileUploadException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ServiceImpl implements S3Service {
    private final S3Config s3Config;

    @Override
    public void uploadFile(ByteArrayOutputStream outputStream, String key) {
        log.debug("Starting file upload to S3 with key: {}", key);
        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(outputStream.size());
            objectMetadata.setContentType(MediaType.IMAGE_JPEG.getType());
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    s3Config.getBucketName(), key, new ByteArrayInputStream(outputStream.toByteArray()), objectMetadata);
            s3Config.getS3Client().putObject(putObjectRequest);
            log.info("File uploaded successfully to S3 with key: {}", key);
        } catch (Exception e) {
            log.error("Failed to upload file to S3 with key: {}", key, e);
            throw new FileUploadException("Failed to upload file");
        }
    }

    @Override
    public void deleteFile(String key) {
        log.debug("Starting file deletion from S3 with key: {}", key);
        s3Config.getS3Client().deleteObject(s3Config.getBucketName(), key);
    }

    @Override
    public InputStream downloadFile(String key) {
        log.debug("Starting file download from S3 with key: {}", key);
        return s3Config.getS3Client().getObject(s3Config.getBucketName(), key).getObjectContent();
    }
}
