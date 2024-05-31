package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("The user with this id was not found: " + userId);
        }
    }

    public void validateUserNotExists(User user) {
        if (userRepository.existsUserByUsername(user.getUsername())) {
            throw new DataValidationException("The user with this username already exists id: " + user.getUsername());
        }
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new DataValidationException("The user with this email already exists id: " + user.getEmail());
        }
        if (userRepository.existsUserByPhone(user.getPhone())) {
            throw new DataValidationException("The user with this phone already exists id: " + user.getPhone());
        }
    }
}
