package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.FollowerEventDto;

import java.time.LocalDateTime;

@Component
public class FollowerEventPublisher extends AbstractEventPublisher<FollowerEventDto>{
    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                  @Value("${spring.data.redis.channels.follower_channel}") String channelTopicName) {
        super(redisTemplate, objectMapper, channelTopicName);
    }

    public void publish(FollowerEventDto followerEventDto) {
        followerEventDto.setSubscriptionTime(LocalDateTime.now());
        publishInTopic(followerEventDto);
    }
}
