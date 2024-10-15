package school.faang.user_service.model.enums;

public enum EventType {
    WEBINAR("Webinar"),
    POLL("Poll"),
    MEETING("Meeting"),
    GIVEAWAY("Giveaway"),
    PRESENTATION("Presentation"),
    ;
    private final String type;

    EventType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return type;
    }
}