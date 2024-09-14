package school.faang.user_service.validator.goal;

import java.util.List;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;

@Component
public class GoalValidator {

    public void validateGoal(GoalDto goalDto) {
        if (goalDto.getTitle() == null || goalDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Название цели не может быть пустым");
        }

        if (goalDto.getDescription() == null || goalDto.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Описание цели не может быть пустым");
        }

    }
}