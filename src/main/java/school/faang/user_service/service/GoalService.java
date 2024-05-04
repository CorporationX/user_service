package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.dto.filter.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.UpdatingCompletedGoalException;
import school.faang.user_service.exceptions.UserGoalsValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.filters.GoalFilter;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final static int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SkillService skillService;
    private final List<GoalFilter> goalFilters;

    public void createGoal(Long userId, Goal goal) {
        if (goalRepository.countActiveGoalsPerUser(userId) >= MAX_ACTIVE_GOALS) {
            throw new UserGoalsValidationException("Max active goals doesnt grand then MAX_ACTIVE_GOALS");
        }
        if (!skillService.checkSkillsInDB(goal.getSkillsToAchieve())) {
            throw new DataValidationException("The goal contains non-existent skills");
        }
        goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getId());
        goal.getSkillsToAchieve()
                .forEach(skill -> goalRepository.addSkillToGoal(skill.getId(), goal.getId()));
    }

    public void deleteGoal(long goalId) {
        goalRepository.deleteGoalById(goalId);
    }

    public void updateGoal(Long goalId, GoalDto goalDto) {
        if (goalRepository.findGoalById(goalId).getStatus()
                .equals(GoalStatus.COMPLETED)) {
            throw new UpdatingCompletedGoalException("This goal completed");
        }
        if (goalDto.getSkillsToAchieve().size() !=
                skillService.checkAmountSkillsInDB(goalDto.getSkillsToAchieve())) {
            throw new DataValidationException("The goal contains non-existent skills");
        }
        if (goalDto.getStatus().equals(GoalStatus.COMPLETED.toString())) {
            List<User> users = goalRepository.findUsersByGoalId(goalId);
            users.forEach(user ->
                    goalDto.getSkillsToAchieve()
                            .forEach(skill ->
                                    skillService.assignSkillToUser(skill, user.getId())));
            //update in table goals
        } else {
            goalRepository.removeSkillsFromGoal(goalId);
            goalDto.getSkillsToAchieve()
                    .forEach(skill ->
                            goalRepository.addSkillToGoal(skill, goalId));
        }
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findByParent(goalId);
        return goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filters))
                .flatMap(goalFilter -> goalFilter.apply(goals, filters))
                .map(goalMapper::toDto)
                .toList();
    }
}
