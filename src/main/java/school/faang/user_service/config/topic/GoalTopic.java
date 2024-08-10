package school.faang.user_service.config.topic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class GoalTopic {

    @Value("${spring.data.redis.channels.goal_channel.name}")
    private String topic;

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic(topic);
    }
}
