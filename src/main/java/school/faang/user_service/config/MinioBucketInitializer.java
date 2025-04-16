package school.faang.user_service.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MinioBucketInitializer {

    private static final String INITIALIZING_BUCKET_LOG = "Initializing bucket {}";
    private static final String BUCKET_ALREADY_EXISTS_LOG = "Bucket '{}' already exists.";
    private static final String BUCKET_CREATED_LOG = "Bucket '{}' created successfully.";
    private static final String BUCKET_INIT_EXCEPTION_MSG = "Error initializing bucket in MinIO";

    @Value("${app.minio.bucket.name}")
    private String bucketName;

    private final MinioClient minioClient;

    @PostConstruct
    public void initializeBucket() {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            System.out.println("ava " + found);
            if (!found) {
                log.info(INITIALIZING_BUCKET_LOG, bucketName);
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info(BUCKET_CREATED_LOG, bucketName);
            } else {
                log.info(BUCKET_ALREADY_EXISTS_LOG, bucketName);
            }
        } catch (Exception e) {
            //log.error(e.getMessage(), e);
        }
    }
}