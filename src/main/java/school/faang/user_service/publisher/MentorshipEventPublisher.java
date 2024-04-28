package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.dto.event.MentorshipStartEvent;

public class MentorshipEventPublisher extends AbstractPublisher<MentorshipStartEvent>{
    public MentorshipEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                    ObjectMapper jsonMapper,
                                    @Value("${spring.data.redis.channels.mentorship_channel.name}")
                                    String mentorshipTopic){
        super(redisTemplate, jsonMapper, mentorshipTopic);
    }
}
