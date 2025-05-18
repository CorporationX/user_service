package school.faang.user_service.configuration.goals.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix = "goals")
public class GoalProperties {

    private long maxActiveGoals;
}
