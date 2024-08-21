package school.faang.user_service.publisher.skillOffer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillOfferedEventDto;
import school.faang.user_service.publisher.RedisAbstractPublisher;

@Component
public class RedisSkillOfferPublisher extends RedisAbstractPublisher<SkillOfferedEventDto> implements SkillOfferPublisher {

    public RedisSkillOfferPublisher(
        RedisTemplate<String, Object> redisTemplate,
        ObjectMapper objectMapper,
        ChannelTopic skillOfferTopic
    ) {
        super(redisTemplate, skillOfferTopic, objectMapper);
    }

    @Override
    public void publish(SkillOfferedEventDto message) {
        publishMessage(message);
    }
}
