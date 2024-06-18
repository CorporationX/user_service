package school.faang.user_service.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SkillAcquiredEventPublisher extends AbstractPublisher {

    public SkillAcquiredEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic skillTopic) {
        super(redisTemplate);
        channelTopic = skillTopic;
    }
}
