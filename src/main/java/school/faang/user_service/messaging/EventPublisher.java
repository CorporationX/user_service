package school.faang.user_service.messaging;

import school.faang.user_service.dto.mentorshipRequest.MentorshipEventDto;

public interface EventPublisher<T> {
    void publish(T t);
}
