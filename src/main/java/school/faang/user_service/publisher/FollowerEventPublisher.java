package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEventDto;

import java.time.LocalDateTime;

@Component
public class FollowerEventPublisher extends AbstractEventPublisher<FollowerEventDto> {

    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String channelTopicName;

    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    public void publish(FollowerEventDto followerEventDto) {
        followerEventDto.setReceivedAt(LocalDateTime.now());
        convertAndSend(followerEventDto, channelTopicName);
    }
}
