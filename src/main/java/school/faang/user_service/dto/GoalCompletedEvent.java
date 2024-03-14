package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Builder
@Data
public class GoalCompletedEvent {
    private long userId;
    private long goalId;
    private LocalDateTime goalCompletedAt;
}
