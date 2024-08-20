package school.faang.user_service.config.redis.recommendation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RecommendationRedisConfig {

    @Value("${spring.data.redis.channels.recommendation_channel.name}")
    private String recommendationChannelName;

    @Bean
    public ChannelTopic recommendationTopic() {
        return new ChannelTopic(recommendationChannelName);
    }
}
