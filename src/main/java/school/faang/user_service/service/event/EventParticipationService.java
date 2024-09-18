package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    public void deleteParticipantsFromEvent(Event event) {
        List<User> attenders = event.getAttendees();
        attenders.forEach(user -> user.getParticipatedEvents().remove(event));
    }
}
