package school.faang.user_service.validator.user;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final EventParticipationRepository eventParticipationRepository;

    public void validateUserRegister(long userId) throws ValidationException {
        if (eventParticipationRepository.existsById(userId)) {
            throw new ValidationException("Пользователь уже зарегистрирован");
        }
    }

    public void validateUserUnregister(long userId) throws ValidationException {
        if (!eventParticipationRepository.existsById(userId)) {
            throw new ValidationException("Пользователь ещё не зарегистрирован");
        }
    }

    public void validateUserId(Long userId) {
        if (userId == null) {
            throw new ValidationException("User id can't be null");
        }
    }
}
