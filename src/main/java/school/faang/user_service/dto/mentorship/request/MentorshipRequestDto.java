package school.faang.user_service.dto.mentorship.request;

import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

@Builder
public record MentorshipRequestDto(
        Long id,
        String description,
        Long requesterId,
        Long receiverId,
        RequestStatus status,
        String rejectionReason) {
}
