package school.faang.user_service.config.api;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(value = "services.imagegenerator")
public class ImageGeneratorProperties extends ApiProperties {
}
