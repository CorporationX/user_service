package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    public void deleteParticipantsFromEvent(Event event) {
        List<User> participants = eventParticipationRepository.findAllParticipantsByEventId(event.getId());

        participants.forEach(participant ->
                eventParticipationRepository.unregister(event.getId(), participant.getId()));
    }
}
