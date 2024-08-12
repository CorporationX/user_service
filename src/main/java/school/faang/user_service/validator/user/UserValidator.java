package school.faang.user_service.validator.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;

@Component
@AllArgsConstructor
@Slf4j
public class UserValidator {
    private final UserRepository userRepository;

    public void validateUserExistById(long id) {
        if (!userRepository.existsById(id)) {
            log.error("ошибка. нет такого id");
            throw new EntityNotFoundException("ошибка. нет такого id");
        }
    }
}