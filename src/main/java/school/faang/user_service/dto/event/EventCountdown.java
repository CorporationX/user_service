package school.faang.user_service.dto.event;

public enum EventCountdown {
    DAYS,
    HOURS,
    MINUTES,
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
