package school.faang.user_service.exceptions;

import lombok.Getter;

@Getter
public enum ExceptionMessages {
    GOAL_TITLE_IS_BLANK("Goal title doesnt be blank"),
    MAX_ACTIVE_GOALS_IS_OVER("You already have the maximum number of active goals"),
    NON_EXIST_SKILLS("The goal contains non-existent skills"),
    GOAL_STATUS_IS_COMPLETED("Goal status is completed");

    private final String message;
    ExceptionMessages(String message) {
        this.message = message;
    }
}
