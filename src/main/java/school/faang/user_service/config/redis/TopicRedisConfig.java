package school.faang.user_service.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class TopicRedisConfig {

    @Bean(name = "premiumChannel")
    public ChannelTopic premiumChannelTopic(@Value("${spring.data.redis.channels.premium-channel.name}") String topicName) {
        return new ChannelTopic(topicName);
    }
}
