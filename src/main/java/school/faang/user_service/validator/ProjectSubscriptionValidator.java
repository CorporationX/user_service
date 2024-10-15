package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class ProjectSubscriptionValidator {
    public void validateUser(long contextUserId, long userId) {
        if (contextUserId != userId) {
            throw new DataValidationException(String.format("A user can only subscribe themselves to a project. " +
                    "Context user id = %d, userId = %d", contextUserId, userId));
        }
    }
}
