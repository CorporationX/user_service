package school.faang.user_service.dto.mentorship;

import school.faang.user_service.entity.RequestStatus;

public record MentorshipRequestFilterDto(
        Long requesterId,
        Long receiverId,
        RequestStatus status
) {
}