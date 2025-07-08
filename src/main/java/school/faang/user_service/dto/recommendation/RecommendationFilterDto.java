package school.faang.user_service.dto.recommendation;

public record RecommendationFilterDto(
        String contentContains,
        Long authorId,
        Long receiverId) {
}