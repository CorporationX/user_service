package school.faang.user_service.dto.mentorship.request;

import lombok.Builder;
import school.faang.user_service.entity.RequestStatus;

@Builder
public record RequestFilterDto(
        Long receiverIdFilter,
        Long requesterIdFilter,
        RequestStatus statusFilter) {
}
