package school.faang.user_service.validator.event;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@RequiredArgsConstructor
public class EventParticipationValidator {

    private final EventParticipationRepository eventParticipationRepository;

    public void validateUserRegister(long userId) throws ValidationException {
        if (eventParticipationRepository.existsById(userId)) {
            throw new ValidationException("User already registered");
        }
    }

    public void validateUserUnregister(long userId) throws ValidationException {
        if (!eventParticipationRepository.existsById(userId)) {
            throw new ValidationException("User not yet registered");
        }
    }
}
