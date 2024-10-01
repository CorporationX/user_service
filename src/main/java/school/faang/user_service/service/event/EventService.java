package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.event.properties.EventProperties;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.filters.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.repository.event.filters.EventFilter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventServiceHelper eventValidator;
    private final EventProperties eventProperties;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final List<EventFilter> eventFilters;

    @Transactional
    public EventDto createEvent(EventDto eventDto) {
        eventValidator.eventDatesValidation(eventDto);

        List<SkillDto> ownerSkillsList = getUserSkills(eventDto.getOwnerId());
        Set<SkillDto> ownerSkillsSet = new HashSet<>(ownerSkillsList);
        eventValidator.relatedSkillsValidation(eventDto, ownerSkillsSet);

        return saveEvent(eventDto);
    }

    @Transactional(readOnly = true)
    public EventDto getEvent(Long eventId) {
        Event event = getEventById(eventId);

        return eventMapper.toDto(event);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsByFilters(EventFilterDto filter) {
        return eventRepository.findAll()
                .stream()
                .filter(events -> isAllMatch(filter, events))
                .map(eventMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        eventValidator.eventExistByIdValidation(eventId);
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.eventDatesValidation(eventDto);

        Long eventDtoId = eventDto.getId();
        eventValidator.eventExistByIdValidation(eventDtoId);

        List<SkillDto> ownerSkillsList = getUserSkills(eventDto.getOwnerId());
        Set<SkillDto> ownerSkillsSet = new HashSet<>(ownerSkillsList);
        eventValidator.relatedSkillsValidation(eventDto, ownerSkillsSet);

        return saveEvent(eventDto);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsOwner(Long userId) {
        return eventRepository.findAllByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventParticipants(Long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    @Transactional
    public void deletePastEvents() {
        List<Long> pastEventsIds = eventRepository.findAll()
                .stream()
                .filter(event -> event.getEndDate().isBefore(LocalDateTime.now()))
                .map(Event::getId)
                .toList();

        ListUtils.partition(pastEventsIds, eventProperties.getSublistSize()).forEach(longList -> {
            ExecutorService executor = Executors.newFixedThreadPool(eventProperties.getThreadsNum());

            try {
                executor.submit(() -> eventRepository.deleteAllById(longList));
            } finally {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    private EventDto saveEvent(EventDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);

        Long ownerId = eventDto.getOwnerId();
        User newEventOwner = userRepository.findById(ownerId).orElseThrow(() ->
                new DataValidationException("User with ID: " + ownerId + " not found")
        );
        event.setOwner(newEventOwner);

        Event savedEvent = eventRepository.save(event);
        return eventMapper.toDto(savedEvent);
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new DataValidationException("Event with ID: " + eventId + " not found.")
        );
    }

    private List<SkillDto> getUserSkills(Long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(skillMapper::toDto)
                .toList();
    }

    private boolean isAllMatch(EventFilterDto filter, Event events) {
        return eventFilters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .allMatch(eventFilter -> eventFilter.applyFilter(events, filter));
    }
}
