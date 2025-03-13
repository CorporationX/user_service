package school.faang.user_service.dto.leaderboard;

public record UserActivityResponseDto(
        Long id,
        Long userId,
        String username,
        String country,
        Long rating
) {
}