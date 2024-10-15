package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.dto.ProfileViewEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProfileViewPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    public void publish(ProfileViewEvent event) {
        String channelName = redisProperties.getChannels().get("project-event-channel");
        redisTemplate.convertAndSend(channelName, event);
        log.info("Published event {}", event);
    } // метод не используется, по-скольку нет сервиса реализующего просмотр профиля пользователя
}
