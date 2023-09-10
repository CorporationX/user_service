package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.redis.MentorshipAcceptedEventDto;

@Component
public class MentorshipAcceptedEventPublisher extends AbstractPublisher<MentorshipAcceptedEventDto> {
    public MentorshipAcceptedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                            ObjectMapper objectMapper,
                                            @Qualifier("mentorshipAcceptedEventChannel") ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
