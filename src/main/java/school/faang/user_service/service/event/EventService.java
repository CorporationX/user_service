package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.EventStartDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.event.EventStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.event.EventStartMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.publisher.event.EventStartPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final EventStartMapper eventStartMapper;
    private final SkillMapper skillMapper;
    private final EventStartPublisher eventStartPublisher;

    public EventDto create(EventDto eventDto) {
        validateEvent(eventDto);
        Event event = eventMapper.toEvent(eventDto);

        return eventMapper.toDto(eventRepository.save(event));
    }

    private void validateEvent(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Please enter title of event");
        }

        if (eventDto.getOwnerId() == null || eventDto.getOwnerId() < 0) {
            throw new DataValidationException("Please enter name of event owner");
        }

        if (eventDto.getStartDate() == null) {
            throw new DataValidationException("Please enter start date of event");
        }

        checkingIfUserHasNecessarySkills(eventDto);
    }

    private void checkingIfUserHasNecessarySkills(EventDto eventDto) {
        User user = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new DataValidationException("User not found. Id " + eventDto.getOwnerId()));

        List<SkillDto> userSkills = skillMapper.toListSkillsDto(user.getSkills());
        boolean anySkillMissing = eventDto.getRelatedSkills().stream().anyMatch(skill -> !userSkills.contains(skill));
        if (anySkillMissing) {
            throw new DataValidationException("User has no related skills. Id " + eventDto.getOwnerId());
        }
    }

    public EventDto getEventById(Long id) {
        return eventMapper.toDto(getEvent(id));
    }

    public List<EventDto> getEventsByFilter(Long eventId, EventFilterDto filters) {
        List<Event> events = eventRepository.findAll();

        if (eventId != null) {
            events = events.stream()
                    .filter(event -> event.getId().equals(eventId))
                    .toList();
        }

        Stream<Event> filteredEvents = applyFilters(events.stream(), filters);

        return filteredEvents
                .map(eventMapper::toDto)
                .toList();
    }

    Stream<Event> applyFilters(Stream<Event> events, EventFilterDto filter) {
        if (filter.getTitlePattern() != null) {
            events = events.filter(event -> event.getTitle().contains(filter.getTitlePattern()));
        }

        if (filter.getOwnerIdPattern() != null) {
            events = events.filter(event -> Objects.equals(event.getOwner().getId(), filter.getOwnerIdPattern()));
        }

        if (filter.getDescriptionPattern() != null) {
            events = events.filter(event -> event.getDescription().contains(filter.getDescriptionPattern()));
        }

        if (filter.getRelatedSkillsPattern() != null) {
            events = events.filter(event -> event.getRelatedSkills().stream()
                    .anyMatch(skill -> skill.getTitle().contains(filter.getRelatedSkillsPattern())));
        }

        if (filter.getLocationPattern() != null) {
            events = events.filter(event -> event.getLocation().contains(filter.getLocationPattern()));
        }
        return events;
    }

    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found. Id: " + eventId));

        eventRepository.delete(event);
    }

    public EventDto updateEvent(EventDto eventDto) {
        validateEvent(eventDto);

        Event event = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        eventMapper.updateDto(eventDto, event);
        Event updatedEvent = eventRepository.save(event);

        return eventMapper.toDto(updatedEvent);
    }

    public List<EventDto> getOwnedEvents(Long userId) {
        List<Event> ownedEvents = eventRepository.findAllByUserId(userId);
        return eventMapper.toListDto(ownedEvents);
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        List<Event> participatedEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toListDto(participatedEvents);
    }
    private final EventAsyncService eventAsyncService;

    public boolean existsById(long id) {
        return eventRepository.existsById(id);
    }

    public Event getEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + id + " not found"));
    }

    @Transactional
    public void clearEvents(int partitionSize) {
        List<Event> events = eventRepository.findAll().stream()
                .filter(event -> event.getEndDate().isBefore(LocalDateTime.now()))
                .toList();

        if (events.size() > partitionSize) {
            log.info("Scheduled clearing of obsolete events, amount: {}", events.size());
            List<List<Event>> partitions = ListUtils.partition(events, partitionSize);
            partitions.forEach(eventAsyncService::clearEventsPartition);
        } else {
            eventAsyncService.clearEventsPartition(events);
        }
    }

    @Transactional(readOnly = true)
    public EventStartDto startEvent(Long id) {
        Event event = getEvent(id);

        if (!event.getStatus().equals(EventStatus.PLANNED)) {
            throw new DataValidationException("You can start only planned events");
        }

        EventStartDto eventStartDto = eventStartMapper.toDto(event);

        eventStartPublisher.publish(eventStartDto);
        return eventStartDto;
    }
}
