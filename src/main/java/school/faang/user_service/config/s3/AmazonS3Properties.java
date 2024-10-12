package school.faang.user_service.config.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("services.s3")
public class AmazonS3Properties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucketName;
}
