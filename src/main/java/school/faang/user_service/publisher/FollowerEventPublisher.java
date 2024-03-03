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

    @Value("${spring.data.redis.channel.follower_channel}")
    private String channelTopicName;

    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(objectMapper, redisTemplate);
    }

    public void publish(FollowerEventDto followerEventDto) {
        followerEventDto.setReceivedAt(LocalDateTime.now());
        publishInTopic(followerEventDto, channelTopicName);
    }
}
