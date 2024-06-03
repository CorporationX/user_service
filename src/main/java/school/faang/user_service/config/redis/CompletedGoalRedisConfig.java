package school.faang.user_service.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.publisher.CompletedGoalPublisher;

@Configuration
public class CompletedGoalRedisConfig {

    @Value("${spring.data.redis.channels.goal_channel.name}")
    private String topicName;

    @Bean
    ChannelTopic completedGoalTopic() {
        return new ChannelTopic(topicName);
    }

    @Bean
    CompletedGoalPublisher completedGoalPublisher(RedisTemplate<String, Object> redisTemplate) {
        return new CompletedGoalPublisher(redisTemplate, completedGoalTopic());
    }
}
