package school.faang.user_service.validation.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventValidator {
    private final SkillRepository skillRepository;

    public void validateUserHasRequiredSkills(EventDto eventDto) {
        Set<Long> userSkillsIds = skillRepository.findAllByUserId(eventDto.getOwnerId()).stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        if (userSkillsIds.isEmpty()) {
            throw new DataValidationException("User hasn't got any skills");
        }
        if (!userSkillsIds.containsAll(eventDto.getRelatedSkillsIds())) {
            throw new DataValidationException("User hasn't got skills required for this event");
        }
    }

    public void validateUserIsOwnerOfEvent(User user, EventDto eventDto) {
        if (user.getId() != eventDto.getOwnerId()) {
            throw new DataValidationException("User is not an owner of the Event");
        }
    }
}
