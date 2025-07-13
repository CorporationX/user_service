package school.faang.user_service.kafka.events;

public enum AnalyticsEventType {
    PROFILE_VIEW,
    EVENT_VIEW,
    PROJECT_VIEW,
    FOLLOWER,
    FOLLOW,
    POST_PUBLISHED,
    POST_VIEW,
    POST_LIKE,
    POST_COMMENT,
    SKILL_RECEIVED,
    RECOMMENDATION_RECEIVED,
    ADDED_TO_FAVOURITES,
    PROJECT_INVITE,
    TASK_COMPLETED,
    GOAL_COMPLETED,
    ACHIEVEMENT_RECEIVED,
    PROFILE_APPEARED_IN_SEARCH,
    PROJECT_APPEARED_IN_SEARCH,
    PREMIUM_BOUGHT;

    public static AnalyticsEventType of(int type) {
        for (AnalyticsEventType analyticsEventType : AnalyticsEventType.values()) {
            if (analyticsEventType.ordinal() == type) {
                return analyticsEventType;
            }
        }
        throw new IllegalArgumentException("Unknown event type: " + type);
    }
}