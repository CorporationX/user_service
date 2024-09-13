package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.exception.DataValidationException;

@Component
@RequiredArgsConstructor
public class GoalValidator {
    public void validateGoalTitle(GoalDto goalDto) throws DataValidationException {
        if (goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            throw new DataValidationException("Title cannot be empty");
        }
    }
}
