package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.SkillOfferEventDto;

@Component
public class SkillOfferedEventPublisher extends AbstractEventPublisher<SkillOfferEventDto> {

    public final ChannelTopic topicSkillOffer;

    @Autowired
    public SkillOfferedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic topicSkillOffer) {
        super(redisTemplate, objectMapper);
        this.topicSkillOffer = topicSkillOffer;
    }

    public void publish(SkillOfferEventDto event) {
        publishInTopic(topicSkillOffer, event);
    }
}
