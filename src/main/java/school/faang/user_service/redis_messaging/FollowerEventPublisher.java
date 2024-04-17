package school.faang.user_service.redis_messaging;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.FollowerEvent;


@Component
@RequiredArgsConstructor

public class FollowerEventPublisher implements MessagePublisher<FollowerEvent>{


    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${data.redis.channels.follower_channel.name}")
    private final String followerChannelTopic;

    @Override
    public void publish(FollowerEvent event) {

        redisTemplate.convertAndSend( followerChannelTopic, event );

    }
}
