package school.faang.user_service.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipOfferedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipOfferedEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final JsonObjectMapper mapper;

    @Value("${spring.data.redis.channels.mentorship_channel.name}")
    private String mentorshipName;

    public void sendEvent(MentorshipOfferedEvent event) {
        log.info("You've received a request for mentoring from {}", event.getRequesterId());

        String json = mapper.writeValueAsString(event);
        redisTemplate.convertAndSend(mentorshipName, json);
    }
}