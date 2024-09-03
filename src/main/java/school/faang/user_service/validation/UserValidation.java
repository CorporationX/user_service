package school.faang.user_service.validation;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidation {

    private final UserRepository userRepository;

    public void validateUserId(long userId) throws ValidationException {
        if (userId < 0) {
            throw new ValidationException("User id can't be less than 0");
        }
    }

    public void validateUserExists(long userId) throws ValidationException {
        userRepository.findById(userId).orElseThrow(() -> new ValidationException("User with id " + userId + " not exists"));
    }
}
