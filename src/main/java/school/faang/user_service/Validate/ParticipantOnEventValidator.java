package school.faang.user_service.Validate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ParticipantOnEventValidator {

    private final EventParticipationRepository eventParticipationRepository;

    public boolean checkParticipantAtEvent(long eventId, long userId) {
        List<User> allParticipantsByEventId = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        return allParticipantsByEventId.stream()
                .anyMatch(user -> user.getId() == userId);
    }
}
