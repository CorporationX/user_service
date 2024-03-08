package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.follower.FollowerEventDto;
@Slf4j
@Component
public class FollowerEventPublisher extends AbstractEventPublisher<FollowerEventDto>{

    @Value("${spring.data.redis.channels.follower_channel.name}")
    private String followerEventChannel;

    public FollowerEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    @Override
    public void publish(FollowerEventDto event) {
        convertAndSend(event, followerEventChannel);
    }
  
}
