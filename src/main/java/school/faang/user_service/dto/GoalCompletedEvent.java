package school.faang.user_service.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
public class GoalCompletedEvent {
    private long userId;
    private long goalId;
    private LocalDateTime timestamp;
}
