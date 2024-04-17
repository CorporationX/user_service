package school.faang.user_service.service.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.FollowerEvent;


@Service
@RequiredArgsConstructor
public class FollowerEventPublisher implements MessagePublisher<FollowerEvent>{


//    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channel.follower_channel.name}")
    private final String followerChannelTopic;

//    public FollowerEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
//        super(objectMapper, redisTemplate);
//    }


    @Override
    public void publish(FollowerEvent event) {

        redisTemplate.convertAndSend( followerChannelTopic, event );

    }
}
