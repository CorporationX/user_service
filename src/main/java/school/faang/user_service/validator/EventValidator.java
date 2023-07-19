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
    private final UserService userService;

    public void validateEvent(EventDto eventDto) {
        if(eventDto == null){
            throw new DataValidationException("Event cannot be null");
        }
        if (eventDto.getTitle() == null || eventDto.getTitle().isBlank()) {
            throw new DataValidationException("Event title cannot be empty");
        }

        if (eventDto.getStartDate() == null) {
            throw new DataValidationException("Event start date cannot be null");
        }

        if (eventDto.getOwnerId() == null || eventDto.getOwnerId() < 0) {
            throw new DataValidationException("Event owner ID cannot be null");
        }
    }

    public void idValidate(Long id) {
        if ( id == null || id < 0) {
            throw new DataValidationException("Id cannot be negative");
        }
    }

    public void checkIfUserHasSkillsRequired(EventDto event) {
        List<Long> skillIds = event.getRelatedSkills().stream()
                .map(SkillDto::getId)
                .toList();
        if (!userService.areOwnedSkills(event.getOwnerId(), skillIds)) {
            throw new DataValidationException("User does not have required skills");
        }
    }
}
