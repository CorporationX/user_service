package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private EventParticipationRepository eventParticipationRepository;

    public List<User> getParticipant(Long eventId) {
        validateEventId(eventId);
        List<User> users = eventParticipationRepository.findAllParticipantsByEventId(eventId);
        List<User> registeredUsers = new ArrayList<>();
        for (User user : users) {
            registeredUsers.add(user);
        }
        return registeredUsers;
    }

    private void validateEventId(Long eventId) {
        if (!eventParticipationRepository.existsById(eventId)) {
            throw new IllegalArgumentException("There is not event with this ID!");
        }
    }
}
