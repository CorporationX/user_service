package school.faang.user_service.dto.leaderboard;

public record UserPopularityResponseDto(
        Long id,
        Long userId,
        String username,
        String country,
        Long impact
) {
}
