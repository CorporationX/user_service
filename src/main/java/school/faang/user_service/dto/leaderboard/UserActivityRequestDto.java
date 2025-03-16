package school.faang.user_service.dto.leaderboard;

public record UserActivityRequestDto(
        Long id,
        Long userId,
        String username,
        String country,
        UserActionDto userAction
) {
}
