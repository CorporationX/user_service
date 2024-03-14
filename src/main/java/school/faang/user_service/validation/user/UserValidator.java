package school.faang.user_service.validation.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateIfUserExistsById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException(String.format("User with id %d doesn't exist", userId));
        }
    }

    private void validateIfUserIsActive(long userId) {
        validateIfUserExistsById(userId);
        if (!userRepository.findById(userId).get().isActive()) {
            throw new DataValidationException(String.format("User with id %d is deactivated", userId));
        }
    }
}
