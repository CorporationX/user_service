package school.faang.user_service.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "services.s3")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AmazonCredentials {

    private String accessKey;
    private String secretKey;
    private String bucketName;
}
