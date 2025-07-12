package school.faang.user_service.exception.recommendation;

import school.faang.user_service.exception.DataValidationException;

public class AnotherAuthorException extends DataValidationException {
    public AnotherAuthorException(String message) {
        super(message);
    }
}
