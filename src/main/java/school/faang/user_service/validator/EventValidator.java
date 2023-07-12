package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

import java.util.List;
@Component
@RequiredArgsConstructor
public class EventValidator {
    private UserService userService;

    public void checkIfUserHasSkillsRequired(EventDto event) {
        List<Long> skillIds = event.getRelatedSkills().stream()
                .map(SkillDto::getId)
                .toList();
        if (!userService.areOwnedSkills(event.getOwnerId(), skillIds)) {
            throw new DataValidationException("User does not have required skills");
        }
    }
}
