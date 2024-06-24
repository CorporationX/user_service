package school.faang.user_service.config.redis.goal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class GoalTopic {

    @Bean
    ChannelTopic completedGoalTopic(@Value("${spring.data.redis.channels.goal_complete_channel.name}") String topicName) {
        return new ChannelTopic(topicName);
    }
}
