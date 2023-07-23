package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.filter.event.EventFilter;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.exception.DataValidException;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper = EventMapper.INSTANCE;
    private final SkillMapper skillMapper = SkillMapper.INSTANCE;
    private final List<EventFilter> filters;

    public EventDto create(EventDto eventDto) {
        validateEventDto(eventDto);
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }

    public EventDto get(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        return EventMapper.INSTANCE.toDto(event);
    }

    public boolean deleteEvent(Long eventId) {
        if (eventRepository.existsById(eventId)) {
            eventRepository.deleteById(eventId);
            return true;
        }
        return false;
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilter) {
        Stream<EventDto> eventDtoStream = eventRepository.findAll().stream().map(eventMapper::toDto);
        for (EventFilter filter : filters) {
            if (filter.isApplicable(eventFilter)) {
                eventDtoStream = filter.apply(eventDtoStream, eventFilter);
            }
        }

        return eventDtoStream.toList();
    }

    public List<EventDto> getOwnedEvents(long userId) {
        return eventMapper.toListDto(eventRepository.findAllByUserId(userId));
    }

    private void validateEventDto(EventDto eventDto) {
        if (eventDto.getId() == null || eventDto.getId() < 1) {
            throw new DataValidException("Event Id must be greater than 0");
        }
        if (eventDto.getTitle().isBlank()) {
            throw new DataValidException("Event must have a title");
        }
        if (eventDto.getStartDate() == null) {
            throw new DataValidException("Event must have a start date");
        }
        if (eventDto.getOwnerId() == null) {
            throw new DataValidException("Event must have a user");
        }
        checkUserContainsSkills(eventDto);
    }

    private void checkUserContainsSkills(EventDto eventDto) {
        User user = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<SkillDto> userSkills = skillMapper.toListSkillsDTO(user.getSkills());
        if (!new HashSet<>(userSkills).containsAll(eventDto.getRelatedSkills())) {
            throw new DataValidException("User has no related skills");
        }
    }
}
