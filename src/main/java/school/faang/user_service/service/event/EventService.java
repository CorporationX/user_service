package school.faang.user_service.service.event;

import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.filter.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.filter.EventFilter;
import school.faang.user_service.service.skill.SkillService;

import java.util.HashSet;
import java.util.List;

import static school.faang.user_service.exception.ExceptionMessage.INAPPROPRIATE_OWNER_SKILLS_EXCEPTION;
import static school.faang.user_service.exception.ExceptionMessage.NO_SUCH_EVENT_EXCEPTION;

@Service
@AllArgsConstructor
@Setter
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final SkillService skillService;
    private List<EventFilter> filters;


    public EventDto create(EventDto event) {
        doesOwnerHaveEventRelatedSkills(event);

        return saveEvent(event);
    }

    public EventDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new DataValidationException(NO_SUCH_EVENT_EXCEPTION.getMessage()));

        return eventMapper.toDto(event);
    }

    public List<EventDto> getEventsByFilter(EventFilterDto filter) {
        var allEvents = eventRepository.findAll();

        return filters.stream()
                .filter(eventFilter -> eventFilter.isApplicable(filter))
                .flatMap(eventFilter -> eventFilter.apply(allEvents, filter))
                .distinct()
                .map(eventMapper::toDto)
                .toList();
    }

    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
    }

    public EventDto updateEvent(EventDto event) {
        if (!eventRepository.existsById(event.getId())) {
            throw new DataValidationException(NO_SUCH_EVENT_EXCEPTION.getMessage());
        }

        doesOwnerHaveEventRelatedSkills(event);

        return saveEvent(event);
    }

    public List<EventDto> getOwnedEvents(Long userId) {
        return eventMapper.toDtoList(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventMapper.toDtoList(eventRepository.findParticipatedEventsByUserId(userId));
    }

    private EventDto saveEvent(EventDto event) {
        return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(event)));
    }

    private void doesOwnerHaveEventRelatedSkills(EventDto event) {
        var ownerSkills = new HashSet<>(skillService.getUserSkills(event.getOwnerId()));

        if (!ownerSkills.containsAll(event.getRelatedSkills())) {
            throw new DataValidationException(INAPPROPRIATE_OWNER_SKILLS_EXCEPTION.getMessage());
        }
    }

}
