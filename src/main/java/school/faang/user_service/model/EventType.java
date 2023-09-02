package school.faang.user_service.model;


public enum EventType {
  PROFILE_VIEW,
  PROJECT_VIEW,
  FOLLOWER,
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

  USER_CREATED,
  USER_DELETED,
  USER_UPDATED,
  USER_DEACTIVATED,

  PREMIUM_PURCHASED,
  PREMIUM_CANCELED,

  EVENT_STARTED;

  public static EventType of(int type) {
    for (EventType eventType : EventType.values()) {
      if (eventType.ordinal() == type) {
        return eventType;
      }
    }
    throw new IllegalArgumentException("Unknown event type: " + type);
  }
}

