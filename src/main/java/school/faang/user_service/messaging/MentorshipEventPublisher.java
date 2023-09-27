package school.faang.user_service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.messaging.events.MentorshipStartEvent;
@Component
public class MentorshipEventPublisher extends AbstractEventPublisher<MentorshipStartEvent>{
    @Autowired
    public MentorshipEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                        ObjectMapper objectMapper,
                                        @Value(value = "${spring.data.redis.channels.mentorship_channel.name}") String chanelName,
                                        RecommendationMapper recommendationMapper) {
        super(redisTemplate, objectMapper, chanelName);
    }

    public void publish(MentorshipStartEvent event) {
        publishInChanel(event);
    }
}
