package school.faang.user_service.config.externalApis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "external.apis")
public class ExternalApisProperties {
    private Map<String, String> urls;
}
