package school.faang.user_service.dto.recommendation;

public record CreateRecommendationDto(
        Long receiverId,
        String content
) {
}
