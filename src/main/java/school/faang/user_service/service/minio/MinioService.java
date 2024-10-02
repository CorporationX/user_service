package school.faang.user_service.service.minio;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.MinioUploadException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
@Service
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;

    public void uploadFile(String fileName, byte[] data, String contentType) {
        try (InputStream inputStream = new ByteArrayInputStream(data)) {
            log.info("Uploading file {} to bucket {}", fileName, bucketName);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, data.length, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error uploading file to MinIO: {}", e.getMessage());
            throw new MinioUploadException("Error uploading file to MinIO", e);
        }
    }
}
