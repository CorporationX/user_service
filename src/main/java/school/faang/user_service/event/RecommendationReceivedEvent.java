package school.faang.user_service.event;

import lombok.Builder;

@Builder
public record RecommendationReceivedEvent(
        long authorId,
        long receiverId,
        long recommendationId
) {
}
