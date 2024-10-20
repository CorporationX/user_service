
import java.time.LocalDateTime;

public record GoalCompletedEventDto(
        long userId,
        long goalId,
        LocalDateTime timestamp) {
}