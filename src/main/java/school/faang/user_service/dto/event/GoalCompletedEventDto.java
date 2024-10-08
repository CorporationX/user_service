package school.faang.user_service.dto.event;

public record GoalCompletedEventDto(
        long userId,
        long eventId) {
}