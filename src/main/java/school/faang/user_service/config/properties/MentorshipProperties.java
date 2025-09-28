package school.faang.user_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "mentorship")
public record MentorshipProperties(
        @DefaultValue("4") int monthsToSubtract
) {
}
