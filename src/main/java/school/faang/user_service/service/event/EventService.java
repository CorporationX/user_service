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

import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
            Set<SkillDto> skills1 = new HashSet<>(target.getRelatedSkills());
            Set<SkillDto> skills2 = new HashSet<>(source.getRelatedSkills());
            if (!skills1.equals(skills2)) {
                target.setRelatedSkills(skills2.stream().toList());
            }
        }

        eventRepository.save(eventMapper.toEntity(target));
        return target;
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
                .orElseThrow(() -> new RuntimeException("User not found. ID: " + eventDto.getOwnerId()));

        List<SkillDto> userSkills = skillMapper.toListSkillsDTO(user.getSkills());
        if (!new HashSet<>(userSkills).containsAll(eventDto.getRelatedSkills())) {
            throw new DataValidException("User has no related skills");
        }
    }
}
