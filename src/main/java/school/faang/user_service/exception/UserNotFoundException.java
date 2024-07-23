package school.faang.user_service.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFoundException extends EntityNotFoundException {
    public UserNotFoundException(long userId) {
        super(String.format("User with ID: %d not found", userId));
        log.error("User with ID: {} not found", userId, this);
    }
}
