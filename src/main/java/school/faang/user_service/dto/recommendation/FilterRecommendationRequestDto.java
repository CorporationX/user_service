package school.faang.user_service.dto.recommendation;

/**
 * Фильтры для поиска рекомендаций.
 */
public record FilterRecommendationRequestDto(
        String contentContains,
        Long authorId,
        Long receiverId
) {
}
