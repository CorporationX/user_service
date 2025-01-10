package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.specification.EventSpecification;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;

    @Transactional
    public EventDto createEvent(EventDto eventDto) {
        Event event = eventMapper.toEntity(eventDto);
        event.setOwner(userService.getUser(eventDto.getOwnerId()));
        event.setRelatedSkills(eventMapper.map(eventDto.getRelatedSkills()));
        return eventMapper.toDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public EventDto getEvent(Long eventId) throws DataValidationException {
        Event event = findEventById(eventId);
        return eventMapper.toDto(event);
    }

    @Transactional
    public EventDto updateEvent(EventDto eventDto) throws DataValidationException {
        Event updatedEvent = eventMapper.toEntity(eventDto);
        updatedEvent.setRelatedSkills(eventMapper.map(eventDto.getRelatedSkills()));
        updatedEvent.setOwner(userService.getUser(eventDto.getOwnerId()));
        return eventMapper.toDto(eventRepository.save(updatedEvent));
    }

    @Transactional
    public void deleteEvent(Long eventId) throws DataValidationException {
        System.out.println("Trying to find event with ID: " + eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found with ID: " + eventId));
        System.out.println("Found event, deleting event.");
        eventRepository.delete(event);
    }
    @Transactional(readOnly = true)
    public List<EventDto> getEventsByFilters(EventFilterDto filterDto) {
        Specification<Event> specification = Specification.where(
                        EventSpecification.hasId(filterDto.getId()))
                .and(EventSpecification.hasTitle(filterDto.getTitle()))
                .and(EventSpecification.hasDescription(filterDto.getDescription()))
                .and(EventSpecification.hasLocation(filterDto.getLocation()))
                .and(EventSpecification.hasMaxAttendees(filterDto.getMaxAttendees()))
                .and(EventSpecification.hasStartDate(filterDto.getStartDate()))
                .and(EventSpecification.hasEndDate(filterDto.getEndDate()))
                .and(EventSpecification.hasEventType(filterDto.getEventType()))
                .and(EventSpecification.hasEventStatus(filterDto.getEventStatus()))
                .and(EventSpecification.hasOwner(filterDto.getOwnerId()))
                .and(EventSpecification.hasSkillIds(filterDto.getSkillIds()));

        List<Event> events = eventRepository.findAll(specification);
        return eventMapper.toDtoList(events);
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsByOwner(Long ownerId) {
        return eventMapper.toDtoList(eventRepository.findAllByUserId(ownerId));
    }

    @Transactional(readOnly = true)
    public List<EventDto> getEventsByParticipant(Long userId) {
        return eventMapper.toDtoList(eventRepository.findParticipatedEventsByUserId(userId));
    }

    private Event findEventById(Long eventId) throws DataValidationException {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException("Event not found with ID: " + eventId));
    }
}

