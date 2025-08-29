package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import school.faang.user_service.event.MentorshipRequestedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestedEventPublisher {

    @Value("${spring.data.redis.channel.mentorship-requested}")
    private String mentorshipRequestedChannel;

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(MentorshipRequestedEvent event) {
        Assert.notNull(event, "Event must not be null");

        try {
            log.info("Отправляю событие запроса на менторство: {} -> {} в топик: {}",
                    event.getSenderId(), event.getReceiverId(), mentorshipRequestedChannel);

            redisTemplate.convertAndSend(mentorshipRequestedChannel, event);
            log.debug("Событие успешно отправлено в топик: {}", mentorshipRequestedChannel);

        } catch (Exception e) {
            log.error("Ошибка при отправке события в Redis топик: {}", mentorshipRequestedChannel, e);
            throw new RuntimeException("Failed to publish mentorship request event", e);
        }
    }
}
