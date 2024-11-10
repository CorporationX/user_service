package school.faang.user_service.validator;

import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    public void validateUserId(long userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be positive");
        }
    }
}
