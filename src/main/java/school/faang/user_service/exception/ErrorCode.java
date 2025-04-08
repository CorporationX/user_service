package school.faang.user_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    /**
     * Ошибки целей
     */
    GOAL_EMPTY_TITLE("Empty title for goal with id"),
    MAX_ACTIVE_GOALS("The number of active goals cannot exceed the allowed value"),
    GOAL_NON_EXISTING_SKILLS("There is a non-existent skill in the goal"),
    GOAL_NOT_FOUND("Goal not found by id");

    private final String description;
}
