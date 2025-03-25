package school.faang.user_service.dto;

import school.faang.user_service.entity.RequestStatus;

public record MentorshipRequestFilterDto(
        String description,
        String requesterUsername,
        String receiverUsername,
        RequestStatus status
) {
}