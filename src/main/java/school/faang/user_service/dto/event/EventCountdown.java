package school.faang.user_service.dto.event;

public enum EventCountdown {
    DAY,
    FIVE_HOURS,
    ONE_HOUR,
    TEN_MINUTES,
    START;

    public static EventCountdown of(int type) {
        for (EventCountdown eventType : EventCountdown.values()) {
            if (eventType.ordinal() == type) {
                return eventType;
            }
        }
        throw new IllegalArgumentException("Unknown event type: " + type);
    }
}
