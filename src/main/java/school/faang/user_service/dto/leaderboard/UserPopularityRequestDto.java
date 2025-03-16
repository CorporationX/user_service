package school.faang.user_service.dto.leaderboard;

public record UserPopularityRequestDto(
        Long id,
        Long userId,
        String username,
        String country,
        UserImpactDto userImpact
) {
}
