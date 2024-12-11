package school.faang.user_service.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.config.scheduler.SchedulerConfig;
import school.faang.user_service.dto.EventDto;
import school.faang.user_service.dto.EventFilterDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EventMapper eventMapper;
    private final SkillMapper skillMapper;
    private final List<EventFilter> eventFilters;
    private final SchedulerConfig schedulerConfig;
    private final ExecutorService executorService;

    public EventDto create(EventDto eventDto) {
        log.info("Creating event:{}", eventDto.toLogString());
        User userOwner = userService.getUserById(eventDto.ownerId());

        log.debug("Validating user skills for owner in createEvent: {}", userOwner.toLogString());
        validateUserHaveSkillsForEvent(userOwner, eventDto);

        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userOwner);

        if (eventExists(event)) {
            log.error("{} already exists in the database", event.toLogString());
            throw new DataValidationException("You do not create event, because this event already create");
        }

        Event savedEvent = eventRepository.save(event);
        log.info("Event saved with : {}", event.toLogString());

        return eventMapper.toDto(savedEvent);
    }

    public List<Event> updateAllEvents(List<Event> events) {
        return eventRepository.saveAll(events);
    }

    public EventDto getEvent(Long eventId) {
        log.info("Fetching event with ID: {}", eventId);
        Event event = getEventById(eventId);
        log.info("Event found by {}", eventId);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        log.info("Fetching events with filters: {}", filters);

        Stream<Event> allEvents = eventRepository.findAll().stream();

        for (EventFilter filter : eventFilters) {
            if (filter.isApplicable(filters)) {
                allEvents = filter.apply(allEvents, filters);
            }
        }

        log.info("Events are filtered");
        return eventMapper.toDtoList(allEvents.toList());
    }

    public void deleteEvent(Long eventId) {
        log.info("Deleting event with ID: {}", eventId);

        Event event = getEventById(eventId);
        User userOwner = event.getOwner();

        userOwner.removeOwnedEvent(event);
        userRepository.save(userOwner);

        event.getAttendees().forEach(attendee -> {
            attendee.removeParticipatedEvent(event);
            userRepository.save(attendee);
        });

        eventRepository.deleteById(eventId);

        log.info("Event with ID: {} deleted", eventId);
    }

    public void deleteAllEvents(List<Event> eventsToDelete) {
        log.info("Deleting events");

        eventRepository.deleteAll(eventsToDelete);

        log.info("Events deleted");
    }


    public EventDto updateEvent(EventDto eventDto) {
        User userOwner = userService.getUserById(eventDto.ownerId());

        log.debug("Validating user skills for owner in updateEvent: {}", userOwner.getId());
        validateUserHaveSkillsForEvent(userOwner, eventDto);

        Event event = getEventById(eventDto.id());

        event.setTitle(eventDto.title());
        event.setStartDate(eventDto.startDate());
        event.setEndDate(eventDto.endDate());
        event.setOwner(userOwner);
        event.setDescription(eventDto.description());
        event.setRelatedSkills(skillMapper.toEntityList(eventDto.relatedSkills()));
        event.setLocation(eventDto.location());
        event.setMaxAttendees(eventDto.maxAttendees());

        Event updatedSavedEvent = eventRepository.save(event);
        log.info("Event {} are updated", event.getTitle());

        return eventMapper.toDto(updatedSavedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        List<Event> ownedEvents = userService.getUserById(userId).getOwnedEvents();
        log.info("Owned Events found by User {}", userId);
        return eventMapper.toDtoList(ownedEvents);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        List<Event> participatedEvents = userService.getUserById(userId).getParticipatedEvents();
        log.info("Participated Events found by {}", userId);
        return eventMapper.toDtoList(participatedEvents);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id).orElseThrow(() -> new
                DataValidationException("Event do not found"));
    }

    public void clearPastEvents() {
        int batchSize = schedulerConfig.getBatchSize();

        List<Event> pastEvents = eventRepository.findByEndDateBefore(LocalDateTime.now());
        if (pastEvents.isEmpty()) {
            log.info("No past events found to clear.");
            return;
        }

        int totalBatches = (int) Math.ceil((double) pastEvents.size() / batchSize);

        for (int i = 0; i < totalBatches; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, pastEvents.size());
            List<Event> batch = pastEvents.subList(start, end);
            executorService.submit(() -> eventRepository.deleteAll(batch));
        }

        executorService.shutdown();
        log.info("Scheduled task to clear past events completed.");
    }

    private void validateUserHaveSkillsForEvent(@NotNull User userOwner, @NotNull EventDto eventDto) {
        Set<SkillDto> relatedSkills = eventDto.relatedSkills() != null ? new HashSet<>(eventDto.relatedSkills())
                : new HashSet<>();

        List<Skill> skills = userOwner.getSkills();
        List<SkillDto> skillDtoList = skillMapper.toDtoList(skills);

        Set<SkillDto> userSkillsDto = skillDtoList != null ? new HashSet<>(skillDtoList)
                : new HashSet<>();

        log.debug("Checking " + "if user has required skills: {}", relatedSkills);
        if (!userSkillsDto.containsAll(relatedSkills)) {
            log.error("{} don't have required skills to create event: {}", userOwner.toLogString(), eventDto.toLogString());
            throw new DataValidationException("User don't have required skills to create event");
        }

        log.info("User {} has all required skills to create event", userOwner.getId());
    }

    private boolean eventExists(Event event) {
        return eventRepository.existsByTitleAndStartDateAndEndDateAndOwnerAndLocationAndMaxAttendees(
                event.getTitle(),
                event.getStartDate(),
                event.getEndDate(),
                event.getOwner(),
                event.getLocation(),
                event.getMaxAttendees()
        );
    }
}