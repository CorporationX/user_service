package school.faang.user_service.publisher;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestedEventDto;
import school.faang.user_service.mapper.JsonObjectMapper;

@Component
@Data
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestedEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.mentorship_requested_channel.name}")
    private String mentorshipRequestedTopic;

    public void publish(MentorshipRequestedEventDto event) {
        String json = objectMapper.toJson(event);
        redisTemplate.convertAndSend(mentorshipRequestedTopic, json);
    }
}
