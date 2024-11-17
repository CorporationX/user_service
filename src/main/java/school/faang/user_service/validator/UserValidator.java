package school.faang.user_service.validator;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;


@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {
    private final UserRepository repository;

    public void isUserExists(Long id) {
        if (!repository.existsById(id)) {
            log.warn("User with id #{} not exists.", id);
            throw new EntityNotFoundException("User with id #" + id + " not exists.");
        }
        log.info("User '{}' exist.", id);
    }

    public void validateUserById(long userId) {
        if (!repository.existsById(userId)) {
            throw new EntityNotFoundException("User with id #" + userId + " not found");
        }
    }
}
