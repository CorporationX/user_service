package school.faang.user_service.publisher.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRequestDto;

public interface MentorshipAcceptEventPublisher {
    void publish(MentorshipRequestDto event);
}
