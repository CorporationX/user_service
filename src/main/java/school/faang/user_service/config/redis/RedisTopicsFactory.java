package school.faang.user_service.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;

@Configuration
public class RedisTopicsFactory {
    @Value("${spring.data.redis.channel-topics.event-start.name}")
    private String eventStartTopicName;

    @Value("${spring.data.redis.channel-topics.mentorship.request_accepted}")
    private String mentorshipRequestAcceptedTopicName;

    @Bean
    public Topic eventStartTopic() {
        return new ChannelTopic(eventStartTopicName);
    }

    @Bean
    public Topic mentorshipRequestAcceptedTopicName() {
        return new ChannelTopic(mentorshipRequestAcceptedTopicName);
    }
}
