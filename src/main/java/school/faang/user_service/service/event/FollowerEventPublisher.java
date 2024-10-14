package school.faang.user_service.service.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.publisher.RedisEventPublisher;

@Service
public class FollowerEventPublisher extends RedisEventPublisher<FollowerEvent> {

    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                  @Qualifier("followerEventTopic") ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }
}