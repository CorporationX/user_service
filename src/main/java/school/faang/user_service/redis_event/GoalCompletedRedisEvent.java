package school.faang.user_service.redis_event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalCompletedRedisEvent implements Serializable {
    private Long userId;
    private Long goalId;
    //private LocalDateTime eventTime;
}
