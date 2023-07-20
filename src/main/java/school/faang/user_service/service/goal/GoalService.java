package school.faang.user_service.service.goal;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
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
                .flatMap(goalFilter -> goalFilter.applyFilter(goals, filters))
                .map(goalMapper::toDto)
                .toList();
    }

    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found by id " + goalId));
        validateGoalBeforeUpdate(goal, goalDto);
        assignSkillsToUsers(goal, goalDto);
        Goal updatedGoal = goalMapper.toEntity(goalDto);
        return goalMapper.toDto(goalRepository.save(updatedGoal));
    }

    private void validateGoalBeforeUpdate(Goal goal, GoalDto goalDto) {
        if (goal.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new IllegalArgumentException("Goal already completed");
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

    private void assignSkillsToUsers(Goal goal, GoalDto updatedGoal) {
        if (updatedGoal.getStatus().equals(GoalStatus.COMPLETED) &&
                !goal.getSkillsToAchieve().isEmpty()) {
            goal.getUsers().forEach(user ->
                    goal.getSkillsToAchieve().forEach(skill -> {
                        if (!skill.getUsers().contains(user))
                            skill.getUsers().add(user);
                    }));
        }
    }
}
