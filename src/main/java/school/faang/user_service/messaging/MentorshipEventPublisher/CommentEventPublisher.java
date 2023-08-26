package school.faang.user_service.messaging.MentorshipEventPublisher;

import school.faang.user_service.dto.mentorshipRequest.MentorshipEventDto;

public interface CommentEventPublisher {
    void publish(MentorshipEventDto mentorshipEventDto);
}
