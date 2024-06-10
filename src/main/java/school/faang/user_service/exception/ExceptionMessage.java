package school.faang.user_service.exception;

public enum ExceptionMessage {
    TITLE_EMPTY_EXCEPTION("Skill title is empty"),
    SKILL_ALREADY_EXIST("Skill has already been added");

    private final String message;

    ExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
