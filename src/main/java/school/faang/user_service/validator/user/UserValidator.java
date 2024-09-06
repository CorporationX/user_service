package school.faang.user_service.validator.user;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import school.faang.user_service.repository.UserRepository;

@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    public void userIdIsPositiveAndNotNullOrElseThrowValidationException(Long userId) throws ValidationException {
        if (userId == null) {
            throw new ValidationException("User id can't be null");
        }
        if (userId < 0) {
            throw new ValidationException("User id can't be less than 0");
        }
    }

    public void userIsExistedOrElseThrowValidationException(Long userId) throws ValidationException {
        userRepository.findById(userId).orElseThrow(() -> new ValidationException("User with id " + userId + " not exists"));
    }


}
