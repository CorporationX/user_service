package school.faang.user_service.validator.user;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    public void checkUserIdIsNotNull(Long userId) {
        if (userId == null) {
            throw new ValidationException("User id can't be null");
        }
    }
}
