package school.faang.user_service.validator.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ExceptionMessages;
import school.faang.user_service.repository.SkillRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventValidator {

    private final SkillRepository skillRepository;

    public void validSkillUserInSkillEvent(EventDto eventDto){
        Set<String> skillNameForEvent = eventDto.getRelatedSkills().stream()
                .map(SkillDto::getTitle)
                .collect(Collectors.toSet());
        Set<String> skillsOwner = skillRepository.findAllByUserId(eventDto.getOwnerId()).stream()
                .map(Skill::getTitle)
                .collect(Collectors.toSet());

        if (!skillsOwner.containsAll(skillNameForEvent)){
            log.error(ExceptionMessages.SKILLS_IN_EVENT_EXCEPTION_FOR_LOG, eventDto.getOwnerId(), skillNameForEvent);
            throw new DataValidationException(ExceptionMessages.SKILLS_IN_EVENT_EXCEPTION);
        }
    }
}
