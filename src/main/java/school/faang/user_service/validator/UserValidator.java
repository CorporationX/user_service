package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public User validateUserExistence(long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            String errMessage = String.format("Could not find user by ID: %d", userId);
            log.error(errMessage);
            throw new RuntimeException(errMessage);
        }
        return userOptional.get();
    }

    public void validateUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(
                    "there is no User with id:\n" +
                            userId);
        }
    }
}
