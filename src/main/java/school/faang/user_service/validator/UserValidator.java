package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserValidator {

    private final UserRepository userRepository;

    public void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new UserValidationException("user id is either null or less than zero");
        }
    }

    public void validateThatUserIdExist(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserValidationException("user wasn't found");
        }
    }

    public Optional<User> findUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        return userOptional;
    }
}
