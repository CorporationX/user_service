package school.faang.user_service.model.event;

import lombok.Builder;

@Builder
public record MentorshipAcceptedEvent(
        long requesterId,
        long receiverId,
        long requestId
) {
}
