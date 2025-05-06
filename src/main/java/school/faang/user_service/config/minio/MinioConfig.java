package school.faang.user_service.config.minio;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class MinioConfig {

    @Value("${app.minio.avatars.endpoint}")
    private String endpoint;

    @Value("${app.minio.avatars.access-key}")
    private String accessKey;

    @Value("${app.minio.avatars.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
