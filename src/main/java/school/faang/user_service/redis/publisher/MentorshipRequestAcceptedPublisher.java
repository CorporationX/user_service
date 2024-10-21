package school.faang.user_service.redis.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestAcceptedDto;
import school.faang.user_service.entity.MentorshipRequest;

@Aspect
@Component
public class MentorshipRequestAcceptedPublisher extends AbstractEventPublisher<MentorshipRequestAcceptedDto> {
    public MentorshipRequestAcceptedPublisher(RedisTemplate<String, Object> redisTemplate,
                                              Topic mentorshipRequestAcceptedTopicName,
                                              ObjectMapper objectMapper) {
        super(redisTemplate, mentorshipRequestAcceptedTopicName, objectMapper);
    }

    @AfterReturning(
            pointcut = "@annotation(school.faang.user_service.annotation.SendMentorshipRequestAccepted)",
            returning = "returnValue"
    )
    public void publishEvent(Object returnValue) {
        MentorshipRequest mentorshipRequest = (MentorshipRequest) returnValue;
                MentorshipRequestAcceptedDto eventDto = MentorshipRequestAcceptedDto.builder()
                        .requestId(mentorshipRequest.getId())
                        .receiverId(mentorshipRequest.getReceiver().getId())
                        .actorId(mentorshipRequest.getRequester().getId())
                        .build();
        publish(eventDto);
    }
}
