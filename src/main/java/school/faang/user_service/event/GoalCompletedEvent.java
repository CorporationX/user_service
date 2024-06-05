package school.faang.user_service.event;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class GoalCompletedEvent {
    private Long userId;
    private Long goalId;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime completedAt;
}
