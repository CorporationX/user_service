package school.faang.user_service.messaging.publisher.mentorship.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.mentorship.request.MentorshipOfferedEvent;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.messaging.publisher.EventPublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipOfferedEventPublisher implements EventPublisher<MentorshipOfferedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic mentorshipOfferedTopic;

    @Override
    public void publish(MentorshipOfferedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(mentorshipOfferedTopic.getTopic(), message);
            log.info("Published MentorshipOffered event: {}", message);
        } catch (JsonProcessingException e) {
            log.error(ExceptionMessages.SERIALIZATION_ERROR + event, e);
        } catch (Exception e) {
            log.error(ExceptionMessages.UNEXPECTED_ERROR + e.getMessage());
        }
    }
}
