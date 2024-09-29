package school.faang.user_service.config.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(value = "services.s3")
public class AmazonS3Properties extends ApiProperties {
}
