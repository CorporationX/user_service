package school.faang.user_service.validator;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.UserValidationException;

@NoArgsConstructor
@Component
public class UserValidator {
    public void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new UserValidationException("user id is either null or less than zero");
        }
    }
}
