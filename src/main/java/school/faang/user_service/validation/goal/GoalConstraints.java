package school.faang.user_service.validation.goal;

import lombok.Getter;

@Getter
public enum GoalConstraints {
    GOAL_TITLE_NULL("Goal title can't be null"),
    GOAL_TITLE_EMPTY("Goal title can't be empty"),
    GOAL_NOT_HAVING_SKILL("Goal must have skill"),
    SKILL_NOT_FOUND("Skill with id \"%s\" does not exist"),
    GOAL_MAXIMUM_PER_USER("Maximum number of user goals reached"),
    GOAL_NOT_FOUND("Goal does not exist"),
    GOAL_STATUS_COMPLETED("Status goal is already: \"Completed\""),
    ENTITY_NOT_FOUND("Entity not found in database");

    private final String message;

    GoalConstraints(String message) {
        this.message = message;
    }
}
