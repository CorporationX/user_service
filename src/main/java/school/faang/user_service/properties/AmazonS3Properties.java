package school.faang.user_service.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("services.s3")
@Component
@Getter
@Setter
public class AmazonS3Properties {
    private String bucketName;
    private String region;
    private String accessKey;
    private String secretKey;
    private String avatarContentType;
}
