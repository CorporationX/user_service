package school.faang.user_service.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.messaging.publisher.mentorship.request.MentorshipAcceptedEventPublisher;

@Configuration
public class MentorshipRedisConfig {

    @Value("${spring.data.redis.channels.mentorship_channel.name}")
    private String mentorshipChannelName;

    @Bean
    public MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                                                             ObjectMapper objectMapper) {
        return new MentorshipAcceptedEventPublisher(redisTemplate, objectMapper, new ChannelTopic(mentorshipChannelName));
    }
}
