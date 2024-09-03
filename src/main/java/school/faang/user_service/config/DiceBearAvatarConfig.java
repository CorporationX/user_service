package school.faang.user_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("avatar-image")
@Data
public class DiceBearAvatarConfig {
    private String style;
    private String format;
}

