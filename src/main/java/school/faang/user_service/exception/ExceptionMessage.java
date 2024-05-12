package school.faang.user_service.exception;

public enum ExceptionMessage {
    TITLE_EMPTY_EXCEPTION("The title skill is empty"),
    SKILL_ALREADY_EXIST("This skill already added");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
