package school.faang.user_service.validation.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validatePassword(String userPassword) {
        if (userPassword.length() < 8) {
            throw new DataValidationException("Password must be at least 8 characters long");
        }
        if (!userPassword.matches(".*[A-Z].*")) {
            throw new DataValidationException("Password must contain at least 1 uppercase letter");
        }
        if (!userPassword.matches(".*\\d.*")) {
            throw new DataValidationException("Password must contain at least 1 digit");
        }
        if (!userPassword.matches(".*[!@#$%^&*()_+<,>./\"'}{;:№].*")) {
            throw new DataValidationException("Password must contain at least 1 special symbol");
        }
    }

    public void validateIfUserExistsById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User with id %d doesn't exist", userId));
        }
    }

    public void validateIfUserIsActive(long userId) {
        validateIfUserExistsById(userId);
        if (!userRepository.findById(userId).get().isActive()) {
            throw new DataValidationException(String.format("User with id %d is deactivated", userId));
        }
    }
}
