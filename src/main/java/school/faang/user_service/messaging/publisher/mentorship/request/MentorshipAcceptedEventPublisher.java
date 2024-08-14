package school.faang.user_service.messaging.publisher.mentorship.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import school.faang.user_service.event.mentorship.request.MentorshipAcceptedEvent;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.exception.event.EventPublishingException;
import school.faang.user_service.messaging.publisher.EventPublisher;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class MentorshipAcceptedEventPublisher implements EventPublisher<MentorshipAcceptedEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic mentorshipTopic;

    @Async
    @Override
    public void publish(MentorshipAcceptedEvent event) {
        String message;
        try {
            message = objectMapper.writeValueAsString(event);
            log.info("Publishing event: {}", event);
            redisTemplate.convertAndSend(mentorshipTopic.getTopic(), message);
        } catch (IOException e) {
            log.error(ExceptionMessages.FAILED_EVENT, e);
            throw new EventPublishingException(ExceptionMessages.FAILED_EVENT, e);
        }
    }
}
