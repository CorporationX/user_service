package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.GoalOverflowException;
import school.faang.user_service.exceptions.SkillNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> filters;

    public void deleteGoal(long goalID) {
        goalRepository.deleteById(goalID);
    }

    public void createGoal(long userId, Goal goal) {
        User user = getUser(userId);
        boolean isExistingSkill = checkExistingSkill(goal);

        if (user.getGoals().size() < 3 && isExistingSkill) {
            goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());
            List<Skill> skills = goal.getSkillsToAchieve().stream()
                    .peek(skill -> skill.getGoals().add(goal))
                    .toList();
            skillService.saveAll(skills);
        } else if (user.getGoals().size() >= 3) {
            throw new GoalOverflowException("Maximum goal limit exceeded. Only 3 goals are allowed.");
        } else {
            throw new SkillNotFoundException("Skill not exist");
        }
    }

    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto goalFilterDto) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);
        return applyFilterAndMapToDto(goalFilterDto, goals);
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId) {
        Stream<Goal> goalParents = goalRepository.findByParent(goalId);
        return goalParents
                .sorted(Comparator.comparing(Goal::getId))
                .map(goalMapper::toDto)
                .toList();
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto goalFilterDto) {
        Stream<Goal> goalParents = goalRepository.findByParent(goalId);
        return applyFiltersToGoalsAndSort(goalFilterDto, goalParents);
    }

    private User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));
    }

    private boolean checkExistingSkill(Goal goal) {
        return goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .allMatch(skillService::checkActiveSkill);
    }

    private List<GoalDto> applyFiltersToGoalsAndSort(GoalFilterDto goalFilterDto, Stream<Goal> goalParents) {
        return filters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(goalFilterDto))
                .flatMap(goalFilter -> goalFilter.applyFilter(goalParents, goalFilterDto))
                .sorted(Comparator.comparing(Goal::getId))
                .map(goalMapper::toDto)
                .toList();
    }

    private List<GoalDto> applyFilterAndMapToDto(GoalFilterDto goalFilterDto, Stream<Goal> goals) {
        return filters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(goalFilterDto))
                .flatMap(goalFilter -> goalFilter.applyFilter(goals, goalFilterDto))
                .map(goalMapper::toDto)
                .toList();
    }
}