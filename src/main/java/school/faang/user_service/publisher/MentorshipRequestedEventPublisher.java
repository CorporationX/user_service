package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestEvent;

@Component
@RequiredArgsConstructor
public class MentorshipRequestedEventPublisher {
    private final ChannelTopic topicMentorshipRequest;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(MentorshipRequestEvent event) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can`t convert JSON to String");
        }
        redisTemplate.convertAndSend(topicMentorshipRequest.getTopic(), json);
    }
}
