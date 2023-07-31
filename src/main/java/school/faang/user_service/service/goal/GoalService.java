package school.faang.user_service.service.goal;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validation.GoalValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final SkillRepository skillRepository;
    private final GoalValidator goalValidator;

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
        goalValidator.validateGoalBeforeUpdate(goal);
        assignSkillsToUsers(goal, goalDto);
        goalMapper.updateFromDto(goalDto, goal);
        return goalMapper.toDto(goalRepository.save(goal));
    }

    private void assignSkillsToUsers(Goal goal, GoalDto updatedGoal) {
        if (updatedGoal.getStatus().equals(GoalStatus.COMPLETED) &&
                !goal.getSkillsToAchieve().isEmpty()) {
            Set<User> users = new HashSet<>(goal.getUsers());
            Set<Skill> skills = new HashSet<>(goal.getSkillsToAchieve());

            for (User user : users) {
                for (Skill skill : skills) {
                    if (!skill.getUsers().contains(user))
                        skill.getUsers().add(user);
                }
            }
        }
    }
}
