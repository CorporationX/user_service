package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventParticipationRepository;

@Component
@RequiredArgsConstructor
public class EventParticipationValidator {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserRepository userRepository;

    public void checkEventIdAndUserIdInDB(Long eventId, Long userId) {
        if (!eventParticipationRepository.existsById(eventId)) {
            throw new DataValidationException("Не найдено событие с id: " + eventId);
        }
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("Не найден пользователь с id: " + userId);
        }
    }
}

