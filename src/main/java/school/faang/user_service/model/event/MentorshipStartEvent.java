package school.faang.user_service.model.event;

import lombok.Builder;

@Builder
public record MentorshipStartEvent(
        long mentorId,
        long menteeId
) {
}
