package school.faang.user_service.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class UserServiceValidator {
    @Value("${system-user-id}")
    private int systemUserId;

    public void validateUser(long contextUserId, long userId) {
        if (contextUserId != userId && contextUserId != systemUserId) {
            throw new DataValidationException(String.format("A regular user can get data only about his own followers. " +
                    "Context user id = %d, userId = %d", contextUserId, userId));
        }
    }
}
