package school.faang.user_service.messaging;

import school.faang.user_service.dto.mentorshipRequest.MentorshipEventDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public abstract class EventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;
    private final JsonMapper mapper;

    public void publish(T event) {
        String json = getJson(event);
        redisTemplate.convertAndSend(topic.getTopic(), json);
    }

    private String getJson(T event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
