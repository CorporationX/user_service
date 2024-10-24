package school.faang.user_service.dto.event;

import lombok.Builder;

@Builder
public record MentorshipAcceptedEventDto(
        Long requestId,
        Long mentorId,
        Long menteeId) {
}
