package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.UserValidationException;
import school.faang.user_service.repository.UserRepository;

@RequiredArgsConstructor
@Component
public class UserValidator {

    private final UserRepository userRepository;

    public void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new UserValidationException("user id is either null or less than zero");
        }
        if (!userRepository.existsById(userId)) {
            throw new UserValidationException("user wasn't found");
        }
    }
}
