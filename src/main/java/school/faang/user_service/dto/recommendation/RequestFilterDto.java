package school.faang.user_service.dto.recommendation;

import lombok.Builder;
import school.faang.user_service.model.RequestStatus;

@Builder
public record RequestFilterDto(
        Long requestId,
        RequestStatus status,
        Long requesterId,
        Long receiverId
) {
}
