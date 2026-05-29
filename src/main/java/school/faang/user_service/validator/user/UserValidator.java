package school.faang.user_service.validator.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;

@RequiredArgsConstructor
@Component
public class UserValidator {
    private final UserContext userContext;

    public void validateUserCompliance(Long userId) {
        if (!(userContext.getUserId() == userId)) {
            throw new IllegalArgumentException("User is not authorized to perform this action.");
        }
    }
}
