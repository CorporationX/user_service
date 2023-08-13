package school.faang.user_service.service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;

@RequiredArgsConstructor
@Service
@Slf4j
public class RedisUserUpdatePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic userTopic;

    public void publish(User user) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            redisTemplate.convertAndSend(userTopic.getTopic(), mapper.writeValueAsString(user));
            log.info("Published user: {}", user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
