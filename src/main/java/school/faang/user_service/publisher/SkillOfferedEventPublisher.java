package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.SkillOfferEventDto;

@Component
public class SkillOfferedEventPublisher extends AbstractEventPublisher<SkillOfferEventDto> {

    @Autowired
    public SkillOfferedEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                      @Value("${spring.data.redis.channels.skill_offer_channel}")
                                      String channelTopicName) {
        super(redisTemplate, objectMapper, channelTopicName);
    }

    public void publish(SkillOfferEventDto event) {
        publishInTopic(event);
    }
}
