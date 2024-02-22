package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.follower.FollowerEventDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowerEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String follower_topic;

    public void publish(FollowerEventDto followerEventDto) {
        try {
            String json = objectMapper.writeValueAsString(followerEventDto);
            redisTemplate.convertAndSend(follower_topic, json);
            log.info("Отправлено событие подписки пользователя с ID: {}, на пользователя с ID: {}",
                    followerEventDto.getFolloweeId(),
                    followerEventDto.getFollowerId());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
