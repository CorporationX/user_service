package school.faang.user_service.message.event.reindex.user;

public enum EventStatus {
    PLANNED("Planned"),
    IN_PROGRESS("In Progress"),
    CANCELED("Canceled"),
    COMPLETED("Completed"),
    ;
    private final String status;

    EventStatus(String type) {
        this.status = type;
    }

    public String getMessage() {
        return status;
    }
}