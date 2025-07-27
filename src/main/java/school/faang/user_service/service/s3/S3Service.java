package school.faang.user_service.service.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.s3.S3UploadResultDto;
import school.faang.user_service.exception.FileException;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "services.s3.enabled", havingValue = "true")
public class S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    private static final long URL_EXPIRATION_MS = 15 * 60 * 1000;

    public S3UploadResultDto uploadFile(byte[] data,
                             String contentType,
                             String folder) {
        ByteArrayInputStream input = new ByteArrayInputStream(data);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(data.length);
        objectMetadata.setContentType(contentType);
        String key = String.format("%s/%d", folder, System.currentTimeMillis());

        try {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, key, input, objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (SdkClientException e) {
            log.error("Error uploading file to S3: {}", e.getMessage());
            throw new FileException(e.getMessage());
        }

        String url = getFileUrl(key);
        return new S3UploadResultDto(key, url);
    }

    public String getFileUrl(String key) {
        log.info("Generate presigned URL for key: {}", key);
        try {
            Date expiration = new Date(System.currentTimeMillis() + URL_EXPIRATION_MS);

            GeneratePresignedUrlRequest request =
                    new GeneratePresignedUrlRequest(bucketName, key)
                            .withMethod(HttpMethod.GET)
                            .withExpiration(expiration);

            URL url = s3Client.generatePresignedUrl(request);
            return url.toString();
        } catch (SdkClientException e) {
            log.error("Error generating presigned URL: {}", e.getMessage());
            throw new FileException(e.getMessage());
        }
    }

    public void deleteFile(String key) {
        log.info("Deleting file from S3 with key: {}", key);
        try {
            s3Client.deleteObject(bucketName, key);
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage());
            throw new FileException(e.getMessage());
        }
    }
}
