package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;
import school.faang.user_service.validation.event.EventValidator;
import school.faang.user_service.validation.user.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillMapper skillMapper;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;
    private final EventValidator eventValidator;
    private final UserValidator userValidator;

    public EventDto create(EventDto eventDto) {
        eventValidator.validateUserHasRequiredSkills(eventDto);
        eventRepository.save(eventMapper.toEntity(eventDto));
        return eventDto;
    }

    public EventDto getEvent(long eventId) {
        eventValidator.validateEventExistsById(eventId);
        Event searchedEvent = eventRepository.findById(eventId).get(); //validateEventExistsById выкинет ошибку, если там null
        return eventMapper.toDto(searchedEvent);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filters) {
        List<Event> events = eventRepository.findAll();
        eventFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(events, filters));
        return eventMapper.toDto(events);
    }

    public void deleteEvent(long eventId) {
        eventValidator.validateEventExistsById(eventId);
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        eventValidator.validateUserHasRequiredSkills(eventDto);
        eventValidator.validateEventExistsById(eventDto.getId());
        Event eventToUpdate = eventRepository.findById(eventDto.getId()).get(); //validateEventExistsById выкинет ошибку, если там null
        eventValidator.validateUserIsOwnerOfEvent(eventToUpdate.getOwner(), eventDto);

        Event updatedEvent = setUpdatedInformation(eventToUpdate, eventDto);
        Event updatedAndSavedEvent = eventRepository.save(updatedEvent);
        return eventMapper.toDto(updatedAndSavedEvent);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        userValidator.validateUserExistsById(userId);
        List<Event> userOwnedEvents = eventRepository.findAllByUserId(userId);
        return eventMapper.toDto(userOwnedEvents);
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        userValidator.validateUserExistsById(userId);
        List<Event> userParticipatedEvents = eventRepository.findParticipatedEventsByUserId(userId);
        return eventMapper.toDto(userParticipatedEvents);
    }

    private Event setUpdatedInformation(Event eventToUpdate, EventDto eventDto) {
        eventToUpdate.setId(eventDto.getId());
        eventToUpdate.setTitle(eventDto.getTitle());
        eventToUpdate.setStartDate(eventDto.getStartDate());
        eventToUpdate.setEndDate(eventDto.getEndDate());
        eventToUpdate.setDescription(eventDto.getDescription());
        eventToUpdate.setRelatedSkills(skillMapper.toEntity(eventDto.getRelatedSkills()));
        eventToUpdate.setLocation(eventDto.getLocation());
        eventToUpdate.setMaxAttendees(eventDto.getMaxAttendees());
        return eventToUpdate;
    }
}
