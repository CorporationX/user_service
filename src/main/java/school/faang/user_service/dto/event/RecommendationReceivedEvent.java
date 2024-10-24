package school.faang.user_service.dto.event;

public record RecommendationReceivedEvent(
        Long recommendationId,
        Long authorId,
        Long recipientId) {
}