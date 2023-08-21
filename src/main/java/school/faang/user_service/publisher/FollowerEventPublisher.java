package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.FollowerEventDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class FollowerEventPublisher {
    private final ObjectMapper mapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private final ChannelTopic topic;

    public void sendEvent(FollowerEventDto event) {
        log.info("User subscription event sending started");
        try {
            String json = mapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic.getTopic(),json);
        } catch (JsonProcessingException exception) {
            exception.printStackTrace();
            log.error("Error while processing JSON: {}", exception.getMessage(), exception);
        }
        log.info("User subscription event sending finished");
    }
}