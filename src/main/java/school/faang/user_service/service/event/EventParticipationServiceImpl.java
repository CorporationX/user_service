package school.faang.user_service.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.event.EventStartEvent;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventParticipationRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventParticipationServiceImpl implements EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;
    private final EventStartEventPublisher eventStartEventPublisher;

    @Transactional
    public void registerParticipant(long eventId, long userId) {
        if (isUserRegisteredForEvent(eventId, userId)) {
            throw new DataValidationException("User is already registered for the event");
        }
        eventParticipationRepository.register(eventId, userId);

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new DataValidationException("Event not found"));

        EventStartEvent eventStartEvent = new EventStartEvent();
        eventStartEvent.setTitle(event.getTitle());
        eventStartEvent.setStartDate(event.getStartDate());
        eventStartEvent.setOwnerId(event.getOwner().getId());
        eventStartEvent.setSubscriberId(userId);

        try {
            String json = objectMapper.writeValueAsString(eventStartEvent);
            eventStartEventPublisher.publish(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
