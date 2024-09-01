package school.faang.user_service.config.topic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RecommendationConfig {
    @Value("${spring.data.redis.channels.recommendation_channel.name}")
    private String channelTopic;

    @Bean
    ChannelTopic recommendationChannel() {
        return new ChannelTopic(channelTopic);
    }
}
