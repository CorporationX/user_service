package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.filter.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.exceptions.UpdatingCompletedGoalException;
import school.faang.user_service.exceptions.UserGoalsValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
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
            throw new UserGoalsValidationException(
                    "You already have the maximum number of active goals ("
                            + MAX_ACTIVE_GOALS + ")");
        }
        if (!skillService.checkSkillsInDB(goal.getSkillsToAchieve())) {
            throwDataValidationExceptionWithMessage();
        }
        goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getId());
        goal.getSkillsToAchieve()
                .forEach(skill -> goalRepository.addSkillToGoal(skill.getId(),
                        goalRepository.findGoalIdByTitle(goal.getTitle())));
    }

    public void deleteGoal(long goalId) {
        goalRepository.deleteGoalById(goalId);
    }

    public void updateGoal(Long goalId, GoalDto goalDto) {
        if (goalRepository.findGoalById(goalId).getStatus()
                .equals(GoalStatus.COMPLETED)) {
            throw new UpdatingCompletedGoalException("This goal completed");
        }
        if (goalDto.getSkillIds().size() !=
                skillService.checkAmountSkillsInDB(goalDto.getSkillIds())) {
            throwDataValidationExceptionWithMessage();
        }
        if (goalDto.getStatus().equals(GoalStatus.COMPLETED.toString())) {
            List<User> users = goalRepository.findUsersByGoalId(goalId);
            users.forEach(user ->
                    goalDto.getSkillIds()
                            .forEach(skill ->
                                    skillService.assignSkillToUser(skill, user.getId())));
            //update in table goals
        } else {
            goalRepository.removeSkillsFromGoal(goalId);
            goalDto.getSkillIds()
                    .forEach(skill ->
                            goalRepository.addSkillToGoal(skill, goalId));
        }
    }

    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findByParent(goalId);
        return applyFilters(goals, filters);
    }

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filters) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);
        return applyFilters(goals, filters);
    }

    private List<GoalDto> applyFilters(Stream<Goal> stream, GoalFilterDto filters) {
        return goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filters))
                .flatMap(goalFilter -> goalFilter.apply(stream, filters))
                .map(goalMapper::toDto)
                .distinct()
                .toList();
    }

    private void throwDataValidationExceptionWithMessage() {
        throw new DataValidationException("The goal contains non-existent skills");
    }
}
