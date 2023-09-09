package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.dto.redis.MentorshipAcceptedEventDto;

public class MentorshipAcceptedEventPublisher extends AbstractPublisher<MentorshipAcceptedEventDto> {
    public MentorshipAcceptedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
