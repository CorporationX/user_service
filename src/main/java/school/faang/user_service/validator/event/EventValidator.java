package school.faang.user_service.validator.event;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final EventParticipationRepository eventParticipationRepository;

    public void checkIfUserRegisterOnEvent(long userId) throws ValidationException {
            if (eventParticipationRepository.existsById(userId)) {
                throw new ValidationException("Пользователь уже зарегистрирован");
            }
    }

    public void checkIfUserUnregisterOnEvent(long userId) throws ValidationException {
        if (!eventParticipationRepository.existsById(userId)) {
            throw new ValidationException("Пользователь ещё не зарегистрирован");
        }
    }

    public void eventIdIsNotNullOrElseThrowValidationException(Long eventId)  {
        if (eventId == null) {
            throw new ValidationException("Event id can't be null");
        }
    }
}