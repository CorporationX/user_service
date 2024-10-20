package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisTopics {
    SEARCH_APPEARANCE("search-appearance"),
    PROFILE_VIEW("profile-view"),
    EVENTS_VIEW("eventStart-event");
    PROFILE_VIEW("profile-view"),
    GOAL_COMPLETED("goal-completed");

    private final String topicName;
}
