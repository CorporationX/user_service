package school.faang.user_service.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import school.faang.user_service.messaging.events.MentorshipStartEvent;

public class MentorshipEventPublisher extends AbstractEventPublisher<MentorshipStartEvent>{

}
