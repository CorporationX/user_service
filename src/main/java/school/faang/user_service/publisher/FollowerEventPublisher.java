package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.FollowerEventDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowerEventPublisher implements MessagePublisher<FollowerEventDto> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Setter
    @Value("${spring.data.redis.channels.follower-event-channel.name}")
    private String topic;


    @Override
    public void publish(FollowerEventDto followerEventDto) {
        try {
            String messageJson = objectMapper.writeValueAsString(followerEventDto);
            log.info(messageJson);
            redisTemplate.convertAndSend(topic, messageJson);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Can't parse message to json for topic %s".formatted(topic));
        }
    }
}
