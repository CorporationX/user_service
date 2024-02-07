package school.faang.user_service.validator.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {

    public void ifUserIdIsValid(long userId) {
        if (userId <= 0) {
            throw new DataValidationException(String.format("User ID - %d not valid", userId));
        }
    }
}
