package school.faang.user_service.event;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class GoalCompletedEvent implements Serializable {
    private long goalId;
    private long userId;
    private String goalName;
}
