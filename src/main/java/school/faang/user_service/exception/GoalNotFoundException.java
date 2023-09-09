package school.faang.user_service.exception;

import java.text.MessageFormat;

public class GoalNotFoundException extends RuntimeException{
    private Long goalId;

    public GoalNotFoundException(Long goalId){
        this.goalId = goalId;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format("Goal {0} not found", goalId);
    }
}
