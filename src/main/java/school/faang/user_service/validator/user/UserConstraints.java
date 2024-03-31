package school.faang.user_service.validator.user;

import lombok.Getter;

@Getter
public enum UserConstraints {
    PASSWORD_SHORT("Password must be at least 8 characters long"),
    PASSWORD_NOT_UPPERCASE("Password must contain at least 1 uppercase letter"),
    PASSWORD_NOT_DIGITS("Password must contain at least 1 digit"),
    PASSWORD_NOT_SPECIAL_SYMBOL("Password must contain at least 1 special symbol");

    private final String message;

    UserConstraints(String message) {
        this.message = message;
    }
}
