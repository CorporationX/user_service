package school.faang.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.event.MentorshipAcceptedEvent;
import school.faang.user_service.publisher.redis.MentorshipAcceptedEventPublisher;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessagePublishService {

    private final MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;
    private final ObjectMapper objectMapper;

    public void publishMentorshipAcceptedEvent(MentorshipRequest mentorshipRequest) {
        MentorshipAcceptedEvent mentorshipAcceptedEvent = new MentorshipAcceptedEvent();
        mentorshipAcceptedEvent.setRequesterId(mentorshipRequest.getRequester().getId());
        mentorshipAcceptedEvent.setReceiverId(mentorshipRequest.getReceiver().getId());
        mentorshipAcceptedEvent.setMentorshipRequestId(mentorshipRequest.getId());
        mentorshipAcceptedEvent.setSendAt(LocalDateTime.now());

        String message = null;
        try {
            message = objectMapper.writeValueAsString(mentorshipAcceptedEvent);
        } catch (JsonProcessingException e) {
            log.warn("There was an exception during conversion CommentEvent with ID = {} to String",
                    mentorshipRequest.getId());
        }
        mentorshipAcceptedEventPublisher.publishMessage(message);
    }
}
