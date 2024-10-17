package school.faang.user_service.model.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MentorshipRequestedEvent(
        long userId,
        long receiverId,
        LocalDateTime requestedAt
) {
}
