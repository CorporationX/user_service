package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipAcceptedEventDto;

@Component
public class MentorshipAcceptedEventPublisher extends AbstractPublisher<MentorshipAcceptedEventDto> {
    public MentorshipAcceptedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                            ObjectMapper objectMapper,
                                            @Value("${spring.data.redis.channels.mentorship-channel.name}") String mentorshipAcceptedChannel) {
        super(redisTemplate, objectMapper, mentorshipAcceptedChannel);
    }
}