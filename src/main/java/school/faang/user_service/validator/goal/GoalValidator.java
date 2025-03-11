package school.faang.user_service.validator.goal;

import com.github.dockerjava.api.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;

@Component
@Slf4j
public class GoalValidator {
    GoalRepository goalRepository;

    private static final int MAX_ACTIVE_GOALS_PER_USER = 3;

    public void validateCountGoals(int activeGoals, long userId) {
        if (activeGoals >= MAX_ACTIVE_GOALS_PER_USER) {
            log.error("Пользователь с идентификатором {} превысил максимальное количество активных целей ", userId);
            throw new IllegalArgumentException("Целей не может быть больше " + MAX_ACTIVE_GOALS_PER_USER);
        }
    }

    public void validateGoalDto(GoalDto goalDto) {
        if (goalDto == null || goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            log.error("Title отсутствует в goalDTO {}", goalDto);
            throw new IllegalArgumentException("Заголовок цели не может быть пустым");
        }
        if (goalDto.getDescription() == null || goalDto.getDescription().isBlank()) {
            log.error("Description отсутствует в goalDTO {}", goalDto);
            throw new IllegalArgumentException("Описание цели не может быть пустым");
        }
    }

    public void validateUpdateStatus(Goal existingGoal, GoalDto goalDto) {
        if (GoalStatus.COMPLETED.equals(existingGoal.getStatus()) && !GoalStatus.ACTIVE.equals(goalDto.getStatus())) {
            throw new BadRequestException("Не удается обновить завершенную цель");
        }
    }

    public void validateGoal(long goalId) {
        if (!goalRepository.existsById(goalId)) {
            throw new IllegalArgumentException("Goal с " + goalId + " отсутствует");
        }
    }
}