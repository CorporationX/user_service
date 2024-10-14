package school.faang.user_service.dto.mentorship.request;

import school.faang.user_service.entity.RequestStatus;

public record RequestFilterDto(
        Long mentorIdFilter,
        Long requesterIdFilter,
        RequestStatus statusFilter) {
}
