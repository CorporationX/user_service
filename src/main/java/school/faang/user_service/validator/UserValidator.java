package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateUserId(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(
                    "there is no User with id:\n" +
                            userId);
        }
    }
}
