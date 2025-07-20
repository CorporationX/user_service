package school.faang.user_service.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "skill")
public record SkillProperties(
        int minOffersRequired
) {
}
