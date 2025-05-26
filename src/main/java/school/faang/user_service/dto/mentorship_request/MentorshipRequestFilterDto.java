package school.faang.user_service.dto.mentorship_request;

import school.faang.user_service.entity.RequestStatus;

public record MentorshipRequestFilterDto(String description, Long requesterId, Long receiverId, RequestStatus status) {
}
