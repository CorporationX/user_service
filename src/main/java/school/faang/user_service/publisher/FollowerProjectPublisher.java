package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerProjectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerProjectPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publishFollowerEvent(FollowerProjectEvent followerProjectEvent) {
        if (followerProjectEvent == null) {
            log.error("ProjectFollowerEvent равен null. Невозможно опубликовать событие.");
            throw new IllegalArgumentException("ProjectFollowerEvent не может быть null");
        }

        try {
            objectMapper.writeValueAsString(followerProjectEvent);
            redisTemplate.convertAndSend("follower_project_channel", followerProjectEvent);
            log.info("Опубликовано событие для OwnerId: {}",
                followerProjectEvent.ownerId(),
                followerProjectEvent.followerId());
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации события подписчика", e);
            throw new RuntimeException("Ошибка сериализации события подписчика", e);
        }
    }
}
