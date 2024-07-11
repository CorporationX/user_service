package school.faang.user_service.validator;

import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    public void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("user id is either null or less than zero");
        }
    }
}
