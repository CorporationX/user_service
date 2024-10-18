package school.faang.user_service.publis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.redis.RedisProperties;
import school.faang.user_service.dto.follower.FollowerEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.follower.FollowerEventMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowerEventPublisher {
    private final RedisProperties redisProperties;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final FollowerEventMapper followerEventMapper;

    public void publishFollower(User follower, Long followeeId) {
        FollowerEventDto message = followerEventMapper.toEventDto(follower, followeeId);
        String valueAsString;
        String followerEventChannel = redisProperties.getFollowerEventChannelName();
        try {
            valueAsString = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(followerEventChannel, valueAsString);
        log.info("Send message to NotificationService: {}", message);
    }
}
