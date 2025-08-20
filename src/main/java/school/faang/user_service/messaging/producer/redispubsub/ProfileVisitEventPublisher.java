package school.faang.user_service.messaging.producer.redispubsub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.messaging.dto.ProfileVisitEvent;
import school.faang.user_service.messaging.dto.SearchAppearanceEvent;
import school.faang.user_service.messaging.producer.EventPublisher;

import java.util.Objects;

/**
 * Паблишер для событий {@link SearchAppearanceEvent}.
 *
 * <p>Отправляет события в Redis Pub/Sub, используя {@link RedisTemplate}.</p>
 *
 * <p>Особенности:
 * <ul>
 *     <li>Если {@code searchedId == visitedId}, событие не публикуется (самопросмотр).</li>
 *     <li>Логирует публикацию события с указанием топика и содержимого.</li>
 * </ul>
 * </p>
 *
 * @author Myrza
 * @since 19=.08.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileVisitEventPublisher implements EventPublisher<ProfileVisitEvent> {
    private final RedisTemplate<String, ProfileVisitEvent> redisTemplate;

    @Value("${kafka.topics.profile-visit}")
    private String topic;

    @Override
    public void publish(ProfileVisitEvent event) {
        if (Objects.equals(event.visitorId(), event.visitedId())) {
            log.info("visit yourself");
            return;
        }

        log.info("publish to '{}' topic, event: {}", topic, event);
        redisTemplate.convertAndSend(topic, event);
    }
}
