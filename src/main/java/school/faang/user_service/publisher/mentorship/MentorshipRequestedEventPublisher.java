package school.faang.user_service.publisher.mentorship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.mentorship.MentorshipRequestedEvent;
import school.faang.user_service.publisher.MessagePublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipRequestedEventPublisher implements MessagePublisher<MentorshipRequestedEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(MentorshipRequestedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelTopic.getTopic(), message);
            log.info("Published MentorshipRequested event: {}", message);
        } catch (JsonProcessingException e) {
            log.error("Ошибка при сериализации объекта", e);
            throw new IllegalArgumentException("Ошибка при сериализации объекта", e);
        } catch (Exception e) {
            log.error("Ошибка  ", e);
            throw new IllegalArgumentException("Ошибка ");
        }
    }
}
