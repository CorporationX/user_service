package school.faang.user_service.publisher;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisTopics {
    SEARCH_APPEARANCE("search-appearance"),
    PROFILE_VIEW("profile-view"),
    EVENTS_START("eventStart-event"),
    GOAL_COMPLETED("goal-completed"),
    RECOMMENDATION_REQUEST_CHANNEL("recommendation-request-channel"),
    FOLLOW_EVENT("follower-event");

    private final String topicName;

}
