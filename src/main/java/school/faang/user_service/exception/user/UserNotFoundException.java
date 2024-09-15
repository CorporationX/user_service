package school.faang.user_service.exception.user;

import org.springframework.http.HttpStatus;
import school.faang.user_service.exception.ApiException;

public class UserNotFoundException extends ApiException {
    private static final String MESSAGE = "User Not Found";

    public UserNotFoundException() {
        super(MESSAGE, HttpStatus.NOT_FOUND);
    }
}
