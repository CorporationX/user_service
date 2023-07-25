package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper = EventMapper.INSTANCE;
    private final SkillMapper skillMapper = SkillMapper.INSTANCE;

    public EventDto create(EventDto eventDto) {
        validateEventDto(eventDto);
        Event event = eventRepository.save(eventMapper.toEntity(eventDto));
        return eventMapper.toDto(event);
    }

    public EventDto updateEvent(EventDto source) {
        validateEventDto(source);

        Event event = eventRepository.findById(source.getId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found. ID: " + source.getId()));
        EventDto target = eventMapper.toDto(event);

        BeanUtils.copyProperties(source, target, "id", "relatedSkills");

        if (source.getRelatedSkills() != null) {
            List<SkillDto> sourceSkills = source.getRelatedSkills();
            sourceSkills.retainAll(target.getRelatedSkills());
            if (!sourceSkills.isEmpty()) {
                target.setRelatedSkills(source.getRelatedSkills());
            }
        }

        Event result = eventRepository.save(eventMapper.toEntity(target));
        return eventMapper.toDto(result);
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
                .orElseThrow(() -> new IllegalArgumentException("User not found. ID: " + eventDto.getOwnerId()));

        List<SkillDto> userSkills = skillMapper.toListSkillsDTO(user.getSkills());
        boolean anySkillMissing = eventDto.getRelatedSkills().stream().anyMatch(skill -> !userSkills.contains(skill));
        if (anySkillMissing) {
            throw new DataValidException("User has no related skills. Id: " + eventDto.getOwnerId());
        }
    }
}
