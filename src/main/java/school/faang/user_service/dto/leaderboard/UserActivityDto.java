package school.faang.user_service.dto.leaderboard;

public record UserActivityDto(
        Long id,
        Long userId,
        String username,
        String country
) {
}
