package school.faang.user_service.service.event;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.event.EventStartEvent;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.EventPublisher;
import school.faang.user_service.publisher.RedisTopics;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;
    private final UserMapper userMapper;
    private final EventPublisher eventPublisher;

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        if (isUserRegisteredForEvent(eventId, userId)) {
            throw new DataValidationException("User is already registered for the event");
        }
        eventParticipationRepository.register(eventId, userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with %s id not found", eventId)));
        pushMessage(event, userId);
    }

    private void pushMessage(Event event, long userId) {
        EventStartEvent eventStartEvent = new EventStartEvent();
        eventStartEvent.setTitle(event.getTitle());
        eventStartEvent.setStartDate(event.getStartDate());
        eventStartEvent.setOwnerId(event.getOwner().getId());
        eventStartEvent.setSubscriberId(userId);

        eventPublisher.publishToTopic(RedisTopics.EVENTS_START.getTopicName(), eventStartEvent);

    }

    @Transactional
    public void unregisterParticipant(long eventId, long userId) {
        if (!isUserRegisteredForEvent(eventId, userId)) {
            throw new DataValidationException("User is not registered for the event");
        }
        eventParticipationRepository.unregister(eventId, userId);
    }

    @Transactional
    public List<UserDto> getParticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public int getParticipantCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }

    private boolean isUserRegisteredForEvent(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId)
                .stream()
                .anyMatch(user -> user.getId() == userId);
    }
}
