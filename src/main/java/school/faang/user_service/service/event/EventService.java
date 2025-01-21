package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventDto create(EventDto event) {
        log.info("Creating new event: {}", event);
        User owner = userRepository.findById(event.getOwnerId())
                .orElseThrow(() -> {
                    log.error("User with id {} not found", event.getOwnerId());
                    return new ResourceNotFoundException("User not found");
                });
        log.info("Event's owner found: id={}, username={}", owner.getId(), owner.getUsername());

        List<Skill> skills = skillRepository.findAllById(event.getRelatedSkills());
        if (skills.size() != event.getRelatedSkills().size()) {
            log.error("User with id {} and name {} does not own all the skills: {}",
                    owner.getId(), owner.getUsername(), event.getRelatedSkills());
            throw new DataValidationException("User does not own all the skills");
        }


        Event newEvent = eventMapper.toEntity(event);
        Event savedEvent = eventRepository.save(newEvent);
        log.info("New event saved in the database: {}", savedEvent);

        EventDto result = eventMapper.toDto(savedEvent);
        log.info("New event created: {}", result);

        return result;
    }

    public EventDto getEvent(long eventId) {
        log.info("Getting event with id: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event with id {} not found", eventId);
                    return new ResourceNotFoundException("Event not found");
                });
        log.info("Event with id {} found: {}", eventId, event);
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        log.info("Getting events by filter: {}", filter);

        List<Event> events = eventRepository.findAll();

        List<Event> filteredEvents = events.stream()
                .filter(event -> filter.getTitle() == null
                        || event.getTitle().contains(filter.getTitle()))
                .filter(event -> filter.getOwnerId() == null
                        || event.getOwner().getId().equals(filter.getOwnerId()))
                .filter(event -> filter.getSkillId() == null
                        || event.getRelatedSkills().stream().anyMatch(skill ->
                        skill.getId() == (filter.getSkillId())))
                .filter(event -> (filter.getStartDate() == null)
                        || event.getStartDate().isAfter(
                        LocalDateTime.ofEpochSecond(filter.getStartDate(), 0, ZoneOffset.UTC)))
                .filter(event -> filter.getEndDate() == null
                        || event.getEndDate().isBefore(
                        LocalDateTime.ofEpochSecond(filter.getEndDate(), 0, ZoneOffset.UTC)))
                .filter(event -> filter.getMinAttendees() == null
                        || event.getAttendees().size() >= filter.getMinAttendees())
                .filter(event -> filter.getMaxAttendees() == null
                        || event.getAttendees().size() <= filter.getMaxAttendees())
                .toList();

        log.info("Events found after filtering: {}", filteredEvents.size());

        return filteredEvents.stream()
                .map(eventMapper::toDto)
                .toList();

    }

    public void deleteEvent(long eventId) {
        log.info("Deleting event with id: {}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.error("Event for deletion with id {} not found", eventId);
                    return new ResourceNotFoundException("Event for deletion not found");
                });

        eventRepository.delete(event);
        log.info("Event with id {} deleted", eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        log.info("Updating event: {}", eventDto);

        Event event = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> {
                    log.error("Event for update with id {} not found", eventDto.getId());
                    return new ResourceNotFoundException("Event for update not found");
                });
        List<Long> requestedSkills = eventDto.getRelatedSkills();
        List<Skill> skills = skillRepository.findAllByUserId(eventDto.getOwnerId());

        if (requestedSkills.stream().anyMatch(skillId ->
                skills.stream().noneMatch(skill -> skill.getId() == (skillId)))) {
            log.error("User with id {} and name {} does not own all the skills for UpdateEvent: {}",
                    eventDto.getOwnerId(), eventDto.getOwnerId(), requestedSkills);
            throw new DataValidationException("User does not own all the skills for UpdateEvent");
        }
        event.setTitle(eventDto.getTitle());
        event.setStartDate(eventDto.getStartDate());
        event.setEndDate(eventDto.getEndDate());
        event.setDescription(eventDto.getDescription());
        event.setRelatedSkills(skillRepository.findAllById(eventDto.getRelatedSkills()));
        event.setLocation(eventDto.getLocation());
        event.setMaxAttendees(eventDto.getMaxAttendees());

        Event updated = eventRepository.save(event);
        log.info("Event updated: {}", updated);

        return eventMapper.toDto(updated);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        log.info("Getting events owned by user with id: {}", userId);

        List<Event> events = eventRepository.findAllByUserId(userId);
        log.info("Events found: {}", events.size());

        return events.stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        log.info("Getting events participated by user with id: {}", userId);

        List<Event> events = eventRepository.findParticipatedEventsByUserId(userId);
        log.info("Events participated by user with id {}: {}", userId, events.size());

        return events.stream()
                .map(eventMapper::toDto)
                .toList();
    }

}
