package school.faang.user_service.model.event;

import lombok.Builder;

@Builder
public record RecommendationRequestedEvent(
        long requesterId,
        long receiverId,
        long requestId
) {}
