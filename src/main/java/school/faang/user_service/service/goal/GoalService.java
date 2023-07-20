package school.faang.user_service.service.goal;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final SkillRepository skillRepository;

    public List<GoalDto> getGoalsByUser(@NotNull Long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findAll().stream();

        return goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filters))
                .flatMap(goalFilter -> goalFilter.applyFilter(goals,filters))
                .map(goalMapper::toDto)
                .toList();
    }

    public void createGoal(@NotNull Long userId, @NotNull Goal goal) {
        validateGoal(userId, goal);
        goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
        saveSkillsForGoal(goal);
    }

    private void validateGoal(Long userId, Goal goal) {
        if (userId == null) {
            throw new IllegalArgumentException("Invalid userId");
        }

        if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Goal title cannot be empty");
        }

        int MAX_COUNT_ACTIVE_GOALS_PER_USER = 3;
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_COUNT_ACTIVE_GOALS_PER_USER) {
            throw new IllegalArgumentException("User cannot have more than 3 active goals");
        }

        List<Skill> skillsToAchieve = goal.getSkillsToAchieve();
        if (skillsToAchieve != null) {
            for (Skill skill : skillsToAchieve) {
                if (!skillRepository.existsByTitle(skill.getTitle())) {
                    throw new IllegalArgumentException("Skill " + skill.getTitle() + " does not exist");
                }
            }
        }
    }

    private void saveSkillsForGoal(Goal goal) {
        List<Skill> skillsToAchieve = goal.getSkillsToAchieve();
        if (skillsToAchieve != null && !skillsToAchieve.isEmpty()) {
            goalRepository.save(goal);
        }
    }
}
