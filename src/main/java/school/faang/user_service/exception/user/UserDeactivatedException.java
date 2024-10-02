package school.faang.user_service.exception.user;

import org.springframework.http.HttpStatus;
import school.faang.user_service.exception.ApiException;

public class UserDeactivatedException extends ApiException {
    private static final String MESSAGE = "The user has already been deactivated";

    public UserDeactivatedException() {
        super(MESSAGE, HttpStatus.CONFLICT);
    }
}
