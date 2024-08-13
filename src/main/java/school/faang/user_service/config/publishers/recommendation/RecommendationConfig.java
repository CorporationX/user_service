package school.faang.user_service.config.publishers.recommendation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class RecommendationConfig {
    @Value("${spring.data.redis.channels.recommendation_received.name}")
    private String recommendationReceivedChannel;

    @Bean
    ChannelTopic recommendationReceivedTopic() {
        return new ChannelTopic(recommendationReceivedChannel);
    }
}