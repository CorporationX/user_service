package school.faang.user_service.config.s3;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class S3Params {

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;

    @Value("${services.s3.endpoint}")
    private String endpoint;
}