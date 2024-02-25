package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserContext userContext;

    public void validateAccessToUser (long userId) {
        if (userId != userContext.getUserId()) {
            throw new SecurityException("User is not logged in");
        }
    }
}
