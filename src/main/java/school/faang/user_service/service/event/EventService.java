package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filters.EventFilter;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillMapper skillMapper;
    private final EventMapper eventMapper;
    private final List<EventFilter> eventFilters;

    public EventDto create(EventDto eventDto) {
        userHasRequiredSkillsValidation(eventDto);
        eventRepository.save(eventMapper.toEntity(eventDto));
        return eventDto;
    }

    public EventDto getEvent(long eventId) throws NoSuchElementException {
        Event searchedEvent = eventRepository.findById(eventId)
                .orElseThrow(NoSuchElementException::new);
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
        if (eventRepository.findById(eventId).isEmpty()) {
            throw new NoSuchElementException("There is no event with id " + eventId));
        }
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto eventDto) {
        userHasRequiredSkillsValidation(eventDto);
        Event eventUpdated = eventRepository.findById(eventDto.getId())
                .orElseThrow(() -> new NoSuchElementException("There is no event with id " + eventDto.getId()
                        + ". Create one first"));

        if (eventDto.getOwnerId() != eventUpdated.getOwner().getId()) {
            throw new IllegalStateException("User is not an owner of the Event");
        }

        eventUpdated.setId(eventDto.getId());
        eventUpdated.setTitle(eventDto.getTitle());
        eventUpdated.setStartDate(eventDto.getStartDate());
        eventUpdated.setEndDate(eventDto.getEndDate());
        eventUpdated.setDescription(eventDto.getDescription());
        eventUpdated.setRelatedSkills(skillMapper.toEntity(eventDto.getRelatedSkills()));
        eventUpdated.setLocation(eventDto.getLocation());
        eventUpdated.setMaxAttendees(eventDto.getMaxAttendees());

        Event updatedAndSaved = eventRepository.save(eventUpdated);
        return eventMapper.toDto(updatedAndSaved);
    }

    private void userHasRequiredSkillsValidation(EventDto eventDto) {
        List<Skill> userSkills = skillRepository.findAllByUserId(eventDto.getOwnerId());
        List<Skill> requiredSkills = eventDto.getRelatedSkills().stream()
                .map(skillMapper::toEntity)
                .toList();

        if (userSkills == null) {
            throw new DataValidationException("User hasn't got any skills");
        }
        if (!userSkills.containsAll(requiredSkills)) {
            throw new DataValidationException("User hasn't got required skills for this event");
        }
    }
}
