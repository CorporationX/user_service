package school.faang.user_service.dto.leaderboard;

public record UserDto(
        Long id,
        Long userId,
        String username,
        String country
) {
}
