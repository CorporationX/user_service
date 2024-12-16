package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SubscribeEventDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnfollowEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(SubscribeEventDto followerEvent) {
        try {
            objectMapper.writeValueAsString(followerEvent);
            redisTemplate.convertAndSend("unfollower_channel", followerEvent);
            log.info("Опубликованное событие подписчика для FollowerId: {} и FolloweeId: {}", followerEvent.getFollowerId(), followerEvent.getFolloweeId());
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации события отписки", e);
            throw new RuntimeException("Ошибка сериализации события отписки", e);
        }
    }
}
