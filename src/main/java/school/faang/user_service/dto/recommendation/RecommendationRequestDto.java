package school.faang.user_service.dto.recommendation;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.RequestStatus;

public record RecommendationRequestDto(
        Long id,
        String message,
        UserDto requester,
        UserDto receiver,
        RequestStatus status
) {
}
