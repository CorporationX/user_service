package school.faang.user_service.service.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.EmptyGoalsException;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.entity.Skill;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GoalService {
    private final GoalRepository goalRepository;

    @Autowired
    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        if (userId == null) {
            throw new IllegalArgumentException("Необходимо указать идентификатор пользователя.");
        }

        if (filter == null) {
            throw new IllegalArgumentException("Необходимо указать фильтр целей.");
        }

        List<GoalDto> goals = goalRepository.findGoalsByUserId(userId)
                .map(goal -> {
                    GoalDto goalDto = convertToDto(goal);

                    goalDto.setSkillIds(goal.getSkillsToAchieve().stream()
                            .map(Skill::getId)
                            .collect(Collectors.toList()));
                    return goalDto;
                })
                .toList();

        if (goals.isEmpty()) {
            throw new EmptyGoalsException("Список целей пользователя пуст.");
        }

        return goals.stream()
                .filter(goalDto -> (filter.getStatus() == null || filter.getStatus() == goalDto.getStatus())
                        && (filter.getTitle() == null || filter.getTitle().equals(goalDto.getTitle()))
                        && (filter.getSkillIds() == null || filter.getSkillIds().isEmpty()
                        || new HashSet<>(filter.getSkillIds()).containsAll(goalDto.getSkillIds()))
                        && (filter.getParentId() == null || Objects.equals(filter.getParentId(), goalDto.getParentId())))
                .toList();
    }


    GoalDto convertToDto(Goal goal) {
        if (goal == null) {
            throw new IllegalArgumentException("Необходимо указать цель.");
        }

        if (goal.getId() == null) {
            throw new IllegalArgumentException("Идентификатор цели не может быть пустым.");
        }

        if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Название цели не может быть пустым.");
        }

        if (goal.getStatus() == null) {
            throw new IllegalArgumentException("Статус цели не может быть пустым.");
        }

        GoalDto dto = new GoalDto();

        dto.setId(goal.getId());
        dto.setDescription(goal.getDescription());
        dto.setParentId(goal.getParent().getId());
        dto.setTitle(goal.getTitle());
        dto.setStatus(goal.getStatus());

        return dto;
    }
}
