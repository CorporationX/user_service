package school.faang.user_service.dto.event;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class GoalCompletedEventDto implements Serializable {
    private long goalId;
    private long userId;
    private String goalName;
}
