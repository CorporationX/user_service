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

import java.util.HashSet;
import java.util.List;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper = EventMapper.INSTANCE;
    private final SkillMapper skillMapper = SkillMapper.INSTANCE;
    private final List<EventFilter> filters;

    public EventDto create(EventDto eventDto) {
        try {
            validateEventDto(eventDto);
            Event event = eventRepository.save(eventMapper.toEntity(eventDto));
            return eventMapper.toDto(event);
        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public List<EventDto> getEventsByFilter(EventFilterDto eventFilter) {
        List<EventDto> eventDtos = eventRepository.findAll().stream().map(eventMapper::toDto).toList();
        filters.stream().filter(filter -> filter.isApplicable(eventFilter))
                .forEach(filter -> filter.apply(eventDtos, eventFilter));
        return eventDtos;
    }

    private void validateEventDto(EventDto eventDto) throws DataFormatException {
        if (eventDto.getId() == null || eventDto.getId() < 1) {
            throw new DataFormatException("Event Id must be greater than 0");
        }
        if (eventDto.getTitle().isBlank()) {
            throw new DataFormatException("Event must have a title");
        }
        if (eventDto.getStartDate() == null) {
            throw new DataFormatException("Event must have a start date");
        }
        if (eventDto.getOwnerId() == null) {
            throw new DataFormatException("Event must have a user");
        }
        checkUserContainsSkills(eventDto);
    }

    private void checkUserContainsSkills(EventDto eventDto) throws DataFormatException {
        User user = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<SkillDto> userSkills = skillMapper.toListSkillsDTO(user.getSkills());
        if (!new HashSet<>(eventDto.getRelatedSkills()).containsAll(userSkills)) {
            throw new DataFormatException("User has no related skills");
        }
    }
}
