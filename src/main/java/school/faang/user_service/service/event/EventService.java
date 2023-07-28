package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.event.DataValidationException;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
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
        validateEvent(eventDto);
        Event event = eventMapper.toEvent(eventDto);
        if (eventRepository.findByEventId(event.getId()).stream()
                .findFirst()
                .isPresent()) {
            throw new DataValidationException("Event already exists");
        }
        EventDto eventDtoActual = eventMapper.toDto(eventRepository.save(event));
        return eventDtoActual;
    }

    private void validateEvent(EventDto eventDto) {
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Event title is required");
        }

        if (eventDto.getOwnerId() == null || eventDto.getOwnerId() < 0) {
            throw new DataValidationException("Event owner is required");
        }
        checkingIfUserHasNecessarySkills(eventDto);
    }

    private void checkingIfUserHasNecessarySkills(EventDto eventDto) {
        User user = userRepository.findById(eventDto.getOwnerId())
                .orElseThrow(() -> new DataValidationException("User not found. Id " + eventDto.getOwnerId()));

        List<SkillDto> userSkills = skillMapper.toListSkillsDto(user.getSkills());
        boolean anySkillMissing = eventDto.getRelatedSkills().stream().anyMatch(skill -> !userSkills.contains(skill));
        if (anySkillMissing) {
            throw new DataValidationException("User has no related skills. Id " + eventDto.getOwnerId());
        }
    }
}
