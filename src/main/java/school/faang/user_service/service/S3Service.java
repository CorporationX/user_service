package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.S3ServiceException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    @Value("${services.s3.bucket}")
    private String bucketName;

    public String uploadFile(InputStream inputStream, long contentLength, String contentType) {
        String uniqueKey = UUID.randomUUID().toString() + "-avatar.svg";

        try (InputStream stream = inputStream) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueKey)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(stream, contentLength));
            log.info("File with key '{}' uploaded successfully to bucket '{}'.", uniqueKey, bucketName);
            return uniqueKey;
        } catch (Exception e) {
            log.error("Error uploading file to S3 with key '{}'.", uniqueKey, e);
            throw new S3ServiceException("Error uploading file to S3", e);
        }
    }

    public Optional<InputStream> downloadFile(String key) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            log.info("File with key '{}' downloaded successfully from bucket '{}'.", key, bucketName);
            return Optional.of(s3Client.getObject(request));
        } catch (NoSuchKeyException e) {
            log.warn("File with key '{}' not found in bucket '{}'.", key, bucketName);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error downloading file with key '{}' from bucket '{}'.", key, bucketName, e);
            throw new S3ServiceException("Error downloading file from S3", e);
        }
    }

    public void deleteFile(String key) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);
            log.info("File with key '{}' deleted successfully from bucket '{}'.", key, bucketName);
        } catch (Exception e) {
            log.error("Error deleting file with key '{}' from bucket '{}'.", key, bucketName, e);
            throw new S3ServiceException("Error deleting file from S3", e);
        }
    }
}
