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
    FOLLOW_EVENT("follower-event"),
    PROJECT_FOLLOWER_CHANNEL("project-follower-chanel");

    private final String topicName;

}
