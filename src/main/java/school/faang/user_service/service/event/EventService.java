package school.faang.user_service.service.event;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.EventMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.event.EventRepository;

import java.util.HashSet;
import java.util.List;

import static school.faang.user_service.exception.ExceptionMessage.INAPPROPRIATE_OWNER_SKILLS_EXCEPTION;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final EventMapper eventMapper;


    public EventDto create(EventDto eventDto) {
        checkOwnerSkills(eventDto.getOwnerId(), eventDto.getRelatedSkills());

        return eventMapper.toDto(eventRepository.save(eventMapper.toEntity(eventDto)));
    }

    private void checkOwnerSkills(Long ownerId, List<SkillDto> relatedSkills) {
        var ownerSkills = new HashSet<>(skillMapper.toDto(skillRepository.findAllByUserId(ownerId)));

        if (!ownerSkills.containsAll(relatedSkills)) {
            throw new DataValidationException(INAPPROPRIATE_OWNER_SKILLS_EXCEPTION.getMessage());
        }
    }
}
