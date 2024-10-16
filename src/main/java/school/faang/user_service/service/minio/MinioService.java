package school.faang.user_service.service.minio;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.MinioException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(inputStream,
                    data.length, -1).contentType(contentType).build());
        } catch (Exception e) {
            log.error("Error uploading file to MinIO: {}", e.getMessage());
            throw new MinioException("Error uploading file to MinIO", e);
        }
    }

    public byte[] downloadFile(String objectKey) {
        byte[] file;
        try (InputStream stream =
                     minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectKey).build())) {
            file = stream.readAllBytes();
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("Error downloading file from MinIO: {}", e.getMessage());
            throw new MinioException("Error downloading file from MinIO", e);
        }
        return file;
    }

    public void deleteFile(String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectKey).build());
        } catch (ErrorResponseException | ServerException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException |
                 XmlParserException e) {
            log.error("Error deleting file from MinIO: {}", e.getMessage());
            throw new MinioException("Error downloading file from MinIO", e);
        }
    }
}
