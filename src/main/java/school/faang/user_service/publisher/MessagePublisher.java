package school.faang.user_service.publisher;

import school.faang.user_service.dto.message.MentorshipRequestMessage;

public interface MessagePublisher {
    void publish(MentorshipRequestMessage mentorshipRequestMessage);
}
