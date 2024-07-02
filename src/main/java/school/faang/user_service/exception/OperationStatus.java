package school.faang.user_service.exception;

public enum OperationStatus {

    ALREADY_REGISTERED_USER_ERROR("User is already registered on event"),
    IS_NOT_REGISTERED_USER_ERROR("User was not registered on event");

    private final String description;

    OperationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
