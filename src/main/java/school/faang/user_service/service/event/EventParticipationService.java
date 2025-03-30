package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.enums.RatingType;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.service.rating.annotation.RatingChanging;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class EventParticipationService {

    private final EventParticipationRepository eventParticipationRepository;

    @RatingChanging(ratingType = RatingType.EVENT_RATING)
    public void registerParticipant(long eventId, long userId) {
        if (eventParticipationRepository.existsUserByEventIdAndUserId(eventId, userId)) {
            throw new IllegalStateException(
                    String.format("User with ID %d is already registered for event with ID %d", userId, eventId)
            );
        }
        eventParticipationRepository.register(eventId, userId);
    }

    @RatingChanging(ratingType = RatingType.EVENT_RATING, positiveAction = false)
    public void unregisterParticipant(long eventId, long userId) {
        if (!eventParticipationRepository.existsUserByEventIdAndUserId(eventId, userId)) {
            throw new IllegalStateException(
                    String.format("User with ID %d is not registered for event with ID %d", userId, eventId)
            );
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> getNumberOfVisitedEventsPerUser(){
        return eventParticipationRepository.countVisitedEventsPerUser();
    }

    public List<User> getParticipants(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId);
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
