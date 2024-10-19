package school.faang.user_service.redis.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestedEventDto;
import school.faang.user_service.entity.MentorshipRequest;

@Aspect
@Component
public class MentorshipRequestPublisher extends AbstractEventPublisher<MentorshipRequestedEventDto> {
    public MentorshipRequestPublisher(RedisTemplate<String, Object> redisTemplate,
                                      Topic mentorshipRequestReceivedTopicName,
                                      ObjectMapper objectMapper) {
        super(redisTemplate, mentorshipRequestReceivedTopicName, objectMapper);
    }

    @AfterReturning(
            pointcut = "@annotation(school.faang.user_service.annotation.SendMentorshipRequestReceived)",
            returning = "returnValue"
    )
    public void publishPostEvent(Object returnValue) {
        MentorshipRequest mentorshipRequest = (MentorshipRequest) returnValue;
        MentorshipRequestedEventDto eventDto = new MentorshipRequestedEventDto(
                mentorshipRequest.getId(),
                mentorshipRequest.getReceiver().getId(),
                mentorshipRequest.getRequester().getId(),
                mentorshipRequest.getCreatedAt());
        publish(eventDto);
    }
}
