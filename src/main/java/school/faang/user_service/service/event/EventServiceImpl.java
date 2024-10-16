package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventStartEventPublisher eventStartEventPublisher;

    @Override
    public List<Event> getPastEvents() {
        return eventRepository.findAllByEndDateBefore(LocalDateTime.now());
    }

    @Override
    public EventDto getEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(()
                -> new EntityNotFoundException("Event not found with id : %s".formatted(eventId)));
        log.debug("Event with id {} not found", event.getId());
        return eventMapper.toEventDto(event);
    }

    @Async("taskExecutor")
    @Override
    public void deleteEventsByIds(List<Long> ids) {
        eventRepository.deleteAllById(ids);
        log.info("Deleted {} past events with ids: {}", ids.size(), ids);
    }

    @Override
    public void startEvent(EventDto eventDto) {
        Event event = eventMapper.toEvent(getEvent(eventDto.eventId()));
        if (EventStatus.IN_PROGRESS.equals(event.getStatus())) {
            throw new IllegalArgumentException("Event with id %s can`t be started because it is %s"
                    .formatted(event.getId(), event.getStatus()));
        }
        if (!isStartedEvent(event.getStartDate())) {
            throw new IllegalArgumentException("Event with id %s is not started".formatted(event.getId()));
        }
        event.setStatus(EventStatus.IN_PROGRESS);
        eventRepository.save(event);
        log.info("Event with id {} started successfully at {}", event.getId(), LocalDateTime.now());
        EventDto publishEventDto = new EventDto
                (eventDto.usersId(), eventDto.eventId(), eventDto.title(), eventDto.startDateTime());
        eventStartEventPublisher.publish(publishEventDto);
    }

    private boolean isStartedEvent(LocalDateTime startDate) {
        return !startDate.isAfter(LocalDateTime.now());
    }
}