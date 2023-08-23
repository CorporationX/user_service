package school.faang.user_service.publisher;

import school.faang.user_service.dto.mentorship.MentorshipRequestedEvent;

public interface MessagePublisher {
    void publish(MentorshipRequestedEvent event);
}
