package school.faang.user_service.config.google;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "google-calendar")
public class GoogleProperties {

    private String applicationName;

    private String credentialsFilePath;

    private String redirectUri;

    private String accessType;

    private String calendarId;
}
