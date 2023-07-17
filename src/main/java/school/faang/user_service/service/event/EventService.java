package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.validator.EventValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventValidator eventValidator;
    private final EventMapper eventMapper;
    private final List<EventFilter> filters = new ArrayList<>();

    public EventDto createEvent(EventDto eventDto) {
        eventValidator.checkIfUserHasSkillsRequired(eventDto);
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.findById(id).orElseThrow(() -> new DataValidationException("Event not found"));
        eventRepository.deleteById(id);
    }

    public EventDto updateEvent(Long id, EventDto eventFormRequest) {
        Event eventForUpdate = eventRepository.findById(id).orElseThrow(() -> new DataValidationException("Event not found"));
        EventDto eventDtoForUpdate = eventMapper.toDto(eventForUpdate);
        updateEventInDb(eventDtoForUpdate, eventFormRequest);

        return eventDtoForUpdate;
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        Stream<Event> eventStream = StreamSupport.stream(eventRepository.findAll().spliterator(), false);
        return filterEvents(eventStream, filter);
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventRepository.findAllByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public List<EventDto> getParticipatedEvents(long userId) {
        return eventRepository.findParticipatedEventsByUserId(userId)
                .stream()
                .map(eventMapper::toDto)
                .toList();
    }

    public EventDto getEvent(long id) {
        Event entity = eventRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Event not found"));
        return eventMapper.toDto(entity);
    }

    private void updateEventInDb(EventDto eventForUpdate, EventDto eventFormRequest) {
        eventValidator.checkIfUserHasSkillsRequired(eventFormRequest);
        if (!(eventFormRequest.getTitle() == null)) {
            eventForUpdate.setTitle(eventFormRequest.getTitle());
        }
        if (!(eventFormRequest.getStartDate() == null)) {
            eventForUpdate.setStartDate(eventFormRequest.getStartDate());
        }
        if (!(eventFormRequest.getEndDate() == null)) {
            eventForUpdate.setEndDate(eventFormRequest.getEndDate());
        }
        if (!(eventFormRequest.getDescription() == null)) {
            eventForUpdate.setDescription(eventFormRequest.getDescription());
        }

        if (!(eventFormRequest.getLocation() == null)) {
            eventForUpdate.setLocation(eventFormRequest.getLocation());
        }

        if (eventFormRequest.getMaxAttendees() >= 0) {
            eventForUpdate.setMaxAttendees(eventFormRequest.getMaxAttendees());
        }

        if (!(eventFormRequest.getRelatedSkills() == null)) {
            eventForUpdate.setRelatedSkills(eventFormRequest.getRelatedSkills());
        }
        eventRepository.save(eventMapper.toEntity(eventForUpdate));
    }

    private List<EventDto> filterEvents(Stream<Event> events, EventFilterDto filter) {
        filters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .forEach(eventFilter -> eventFilter.applyFilter(events, filter));
        return events
                .map(eventMapper::toDto)
                .toList();
    }
}