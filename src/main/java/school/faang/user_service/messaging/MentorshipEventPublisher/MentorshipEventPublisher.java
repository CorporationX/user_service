package school.faang.user_service.messaging.MentorshipEventPublisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshipRequest.MentorshipEventDto;
import school.faang.user_service.messaging.EventPublisher;

@Component
@Slf4j
@AllArgsConstructor
public class MentorshipEventPublisher implements EventPublisher<MentorshipEventDto> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic mentorshipEventTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(MentorshipEventDto mentorshipEventDto) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(mentorshipEventDto);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
        redisTemplate.convertAndSend(mentorshipEventTopic.getTopic(), json);
    }
}
