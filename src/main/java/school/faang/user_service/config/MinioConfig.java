package school.faang.user_service.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfig {
    private static final String INITIALIZING_BUCKET_LOG = "Initializing bucket {}";
    private static final String BUCKET_ALREADY_EXISTS_LOG = "Bucket '{}' already exists.";
    private static final String BUCKET_CREATED_LOG = "Bucket '{}' created successfully.";

    private static final String BUCKET_INIT_EXCEPTION_MSG = "Error initializing bucket in MinIO";

    @Value("${app.minio.avatars.endpoint}")
    private String endpoint;

    @Value("${app.minio.avatars.access-key}")
    private String accessKey;

    @Value("${app.minio.avatars.secret-key}")
    private String secretKey;

    @Value("${app.minio.bucket.name}")
    private String bucketName;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    @PostConstruct
    public void initializeBucket() {
        try {
            boolean found = minioClient().bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            if (!found) {
                log.info(INITIALIZING_BUCKET_LOG, bucketName);
                minioClient().makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info(BUCKET_CREATED_LOG, bucketName);
            } else {
                log.info(BUCKET_ALREADY_EXISTS_LOG, bucketName);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(BUCKET_INIT_EXCEPTION_MSG, e);
        }
    }
}
