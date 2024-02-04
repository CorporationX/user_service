package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;

    public void registerParticipant(long eventId, long userId) {
        Optional<User> userById = eventParticipationRepository.findById(userId);

        if (userById.isEmpty()) {
            eventParticipationRepository.register(eventId, userId);
        } else {
            throw new IllegalArgumentException("Юзер зарегистрирован");
        }
    }
}
