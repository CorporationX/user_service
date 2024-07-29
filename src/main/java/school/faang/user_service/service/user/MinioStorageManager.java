package school.faang.user_service.service.user;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.StorageException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

@Component
@RequiredArgsConstructor
@Slf4j
public class MinioStorageManager implements StorageManager {

    private final MinioClient minioClient;

    @Override
    public void putObject(String bucketName, String filePath, InputStream content) {
        try {
            createBucketIfNotExist(bucketName);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(filePath)
                            .stream(content, content.available(), -1)
                            .contentType(URLConnection.guessContentTypeFromName(filePath))
                            .build()
            );
            log.info("Successfully uploaded object: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to upload object: {}", filePath, e);
            throw new StorageException("Error while uploading object to Minio", e);
        } catch (Exception e) {
            log.error("An error occurred while handling object: {}", filePath, e);
            throw new StorageException("Failed to put object", e);
        }
    }

    private void createBucketIfNotExist(String bucketName) throws Exception {
        if (isBucketNotExist(bucketName)) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("Bucket created: {}", bucketName);
        }
    }

    private boolean isBucketNotExist(String bucketName) throws Exception {
        return !minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }
}
