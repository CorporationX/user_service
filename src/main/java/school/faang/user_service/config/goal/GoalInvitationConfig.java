package school.faang.user_service.config.goal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "goal.invitation")
public class GoalInvitationConfig {

    private int maxActiveGoals;
}
