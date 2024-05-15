package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void checkUserInDB(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("Не найден пользователь с данным id: " + userId);
        }
    }
}
