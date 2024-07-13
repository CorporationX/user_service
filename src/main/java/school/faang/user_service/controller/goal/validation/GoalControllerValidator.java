package school.faang.user_service.controller.goal.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.InvalidRequestParams;

@Slf4j
@Component
public class GoalControllerValidator {

    public void validateCreation(GoalDto goalDto) {
        validateTitle(goalDto);
        validateDescription(goalDto);
    }

    public void validateUpdating(GoalDto goalDto) {
        validateTitle(goalDto);
        validateDescription(goalDto);
    }

    private void validateTitle(GoalDto goalDto) {
        if (goalDto.getTitle().isEmpty()) {
            log.info("title is empty");
            throw new InvalidRequestParams("Title is empty");
        }
    }

    private void validateDescription(GoalDto goalDto) {
        if (goalDto.getDescription().isEmpty()) {
            log.info("description is empty");
            throw new InvalidRequestParams("Description is empty");
        }
    }
}
