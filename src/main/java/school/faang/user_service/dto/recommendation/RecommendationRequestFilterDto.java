package school.faang.user_service.dto.recommendation;

import jakarta.validation.constraints.AssertTrue;
import school.faang.user_service.entity.RequestStatus;

public record RecommendationRequestFilterDto(
        Long requesterId,
        Long receiverId,
        String messageContains,
        RequestStatus status
) {
    @AssertTrue(message = "Either Requester or Received ID must be provided.")
    public boolean isValidIds() {
        return requesterId != null || receiverId != null;
    }
}
