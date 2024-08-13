package school.faang.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RecommendationSentRedisConfig {
    @Value("${spring.data.redis.channels.recommendation_view_channel.name}")
    private String channelTopic;

    @Bean
    public ChannelTopic recommendationChannelTopic() {
        return new ChannelTopic(channelTopic);
    }
}
