package school.faang.user_service.validation.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.repository.UserRepository;

@Component
@AllArgsConstructor
@Slf4j
public class UserValidator {

    private final UserRepository userRepository;

    public boolean doesUserExistsById(long id) {
        return userRepository.existsById(id);
    }

    public void validateUserExistById(long id) {
        if (!userRepository.existsById(id)) {
            String errorMessage = "User ID = " + id + "Doesn't exist in the system";
            log.error(errorMessage);
            throw new EntityNotFoundException(errorMessage);
        }
    }

}
