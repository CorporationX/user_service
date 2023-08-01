package school.faang.user_service.service.event;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.filter.event.Filter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final List<Filter<Event, EventFilterDto>> filters;

    @Transactional
    public EventDto create(EventDto event) {
        validateEventSkills(event);

        var attendees = userRepository.findAllById(event.getAttendees());
        Event eventEntity = eventMapper.toEntity(event);
        eventEntity.setAttendees(attendees);
        eventEntity = eventRepository.save(eventEntity);

        Event finalEventEntity = eventEntity;
        attendees.forEach(user -> user.getParticipatedEvents().add(finalEventEntity));
        userRepository.saveAll(attendees);

        return eventMapper.toDto(eventEntity);
    }

    public EventDto getEvent(long eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id: %d was not found", eventId)));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        List<Event> events = eventRepository.findAll();
        var filteredEvents = filters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .reduce(events.stream(), (stream, eventFilter) -> eventFilter.applyFilter(stream, filter), Stream::concat)
                .map(eventMapper::toDto)
                .toList();
        return filteredEvents;
    }

    public void deleteEvent(long id) {
        eventRepository.deleteById(id);
    }

    public void updateEvent(EventDto eventDto) {
        validateEventSkills(eventDto);
        eventRepository.save(eventMapper.toEntity(eventDto));
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId).stream().map(eventMapper::toDto).toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId).stream().map(eventMapper::toDto).toList();
    }

    private void validateEventSkills(EventDto event) {
        long ownerId = event.getOwnerId();
        var relatedSkills = event.getRelatedSkills();
        var userSkills = skillRepository.findAllByUserId(ownerId);

        for (SkillDto skill : relatedSkills) {
            boolean userHasSkill = userSkills.stream()
                    .anyMatch(userSkill -> userSkill.getId() == skill.getId());
            if (!userHasSkill) {
                throw new DataValidationException("User does not have the required skills for the event");
            }
        }
    }
}
