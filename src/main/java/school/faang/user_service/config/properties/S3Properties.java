package school.faang.user_service.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "services.s3")
public class S3Properties {
    private String accessKey;
    private String secretKey;
    private String bucketName;
    private String endpoint;
}
