package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.async.AsyncConfig;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventStartEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.publisher.EventStartEventPublisher;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.EventValidator;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventValidator eventValidator;
    private final UserService userService;
    private final List<EventFilter> eventFilters;
    private final AsyncConfig asyncConfig;
    private final EventStartEventPublisher eventStartEventPublisher;

    @Transactional
    public EventDto updateEvent(EventDto eventDto) {
        Event event = getEvent(eventDto.getId());
        eventValidator.validateEventToUpdate(eventDto);
        User owner = userService.getUserById(eventDto.getOwnerId());
        event.setOwner(owner);
        event.setStartDate(eventDto.getStartDate());

        return eventMapper.toDto(eventRepository.save(event));
    }

    @Transactional
    public List<EventDto> getOwnedEvents(long userId) {
        return eventMapper.toListDto(eventRepository.findAllByUserId(userId));
    }

    @Transactional
    public EventDto create(EventDto eventDto) {
        eventValidator.checkIfOwnerExistsById(eventDto.getOwnerId());
        eventValidator.checkIfOwnerHasSkillsRequired(eventDto);
        Event eventEntity = eventMapper.toEntity(eventDto);
        Event createdEvent = eventRepository.save(eventEntity);
        publishEventStartEvent(createdEvent);
        return eventMapper.toDto(createdEvent);
    }

    @Transactional
    public List<EventDto> getParticipatedEventsByUserId(long userId) {
        List<Event> participatedEventsByUserId = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toListDto(participatedEventsByUserId);
    }

    public Event getEvent(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Not found event by Id - " + eventId));
    }

    public EventDto getEventDto(long eventId) {
        Event event = getEvent(eventId);
        return eventMapper.toDto(event);
    }

    @Transactional
    public void deleteEventById(long eventId) {
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public List<EventDto> getEventsByFilter(EventFilterDto filterDto) {
        Stream<Event> events = eventRepository.findAll().stream();

        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(filterDto)) {
                events = eventFilter.apply(events, filterDto);
            }
        }
        return eventMapper.toListDto(events.collect(Collectors.toList()));
    }

    @Async
    public void deletePastEvents(int batchSize) {
        EventFilterDto filters = EventFilterDto.builder()
                .endDatePattern(LocalDateTime.now())
                .build();
        List<Event> pastEvents = eventMapper.toListEntity(getEventsByFilter(filters));

        Executor executor = asyncConfig.getAsyncExecutor();

        List<List<Event>> partitions = ListUtils.partition(pastEvents, batchSize);
        for (List<Event> sublist : partitions) {
            if (executor != null) {
                executor.execute(() -> eventRepository.deleteAll(sublist));
            }
        }
    }

    private void publishEventStartEvent(Event createdEvent) {
        EventStartEventDto eventDto = EventStartEventDto.builder()
                .eventId(createdEvent.getId())
                .attendeesId(createdEvent.getAttendees().stream().map(User::getId).toList())
                .build();

        Date startDate = toDate(createdEvent.getStartDate());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                eventStartEventPublisher.publish(eventDto);
            }
        }, startDate);
    }

    public static Date toDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }
}
