package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final EventMapper eventMapper;

    public EventDto create(EventDto eventDto) {
        return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(userHasRequiredSkills(eventDto))));
    }

    private EventDto userHasRequiredSkills(EventDto eventDto) {
        List<Skill> userSkills = skillRepository.findAllByUserId(eventDto.getOwnerId());
        List<Skill> requiredSkills = eventDto.getRelatedSkills().stream()
                .map(skillMapper::toEntity)
                .toList();
        if (!userSkills.containsAll(requiredSkills)) {
            throw new DataValidationException("User hasn't got required skills for this event");
        }
        return eventDto;
    }
}
