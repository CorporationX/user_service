package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EventNotFoundException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final EventMapper eventMapper;

    private final SkillMapper skillMapper;

    private final List<EventFilter> eventFilters;

    public EventDto createEvent(EventDto eventDto) {
        checkUserSkills(eventDto);
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }


    public EventDto getEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event by ID: " + eventId + " not found"));
        return eventMapper.toDto(event);
    }

    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event by ID: " + eventId + " not found for deleted");
        }
        eventRepository.deleteById(eventId);
    }


    public EventDto updateEvent(EventDto eventDto) {
        checkUserSkills(eventDto);
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }

    public List<EventDto> getOwnedEvents(Long userId) {
        return eventMapper.toListEventDto(eventRepository.findAllByUserId(userId));
    }

    public List<EventDto> getParticipatedEvents(Long userId) {
        return eventMapper.toListEventDto(eventRepository.findParticipatedEventsByUserId(userId));
    }


    public List<EventDto> getEventsByFilter(EventFilterDto eventFilterDto) {
        Stream<Event> eventStream = eventRepository.findAll().stream();

        for (EventFilter eventFilter : eventFilters) {
            if (eventFilter.isApplicable(eventFilterDto)) {
                eventStream = eventFilter.apply(eventStream, eventFilterDto);
            }
        }
        return eventMapper.toListEventDto(eventStream.toList());
    }


    private void checkUserSkills(EventDto eventDto) {
        User user = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new UserNotFoundException("User by ID: " + eventDto.getOwnerId() + " not found"));

        List<SkillDto> userSkills = skillMapper.toListSkillDto(user.getSkills());

        if (!userSkills.containsAll(eventDto.getRelatedSkills())) {
            throw new DataValidationException("User by ID: " + eventDto.getOwnerId() + " does not possess all required skills for this event");
        }
    }


}



