package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.events.MentorshipOfferedEvent;
import school.faang.user_service.publishers.EventJsonConverter;
import school.faang.user_service.publishers.MentorshipOfferedPublisher;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipOfferedEventService {
    private final MentorshipOfferedPublisher mentorshipOfferedPublisher;
    private final EventJsonConverter<MentorshipOfferedEvent> jsonConverter;

    public void publishEvent(MentorshipRequest request) {
        MentorshipOfferedEvent mentorshipOfferedEvent = new MentorshipOfferedEvent();
        mentorshipOfferedEvent.setAuthorId(request.getRequester().getId());
        mentorshipOfferedEvent.setMentorId(request.getReceiver().getId());
        mentorshipOfferedEvent.setRequestId(request.getId());
        String message = jsonConverter.toJson(mentorshipOfferedEvent);
        mentorshipOfferedPublisher.publish(message);
    }
}
