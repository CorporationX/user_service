package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.filter.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filters.GoalFilter;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final SkillService skillService;
    private final List<GoalFilter> goalFilters;
    private final ValidationGoalService validationGoal;

    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        validationGoal.checkMaxActiveGoals(goalRepository.countActiveGoalsPerUser(userId));

        List<Skill> skills = goalDto.getSkillIds().stream()
                .map(skillService::getSkillById)
                .toList();
        //создается с parent_id 0 и кидает ошибку из-за этого
        goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getId());
        skills.forEach(skill ->
                goalRepository.addSkillToGoal(skill.getId(),
                        goalRepository.findGoalIdByTitle(goalDto.getTitle())));

        return goalDto;
    }

    public void deleteGoal(long goalId) {
        goalRepository.deleteGoalById(goalId);
    }

    public void updateGoal(Long goalId, GoalDto goalDto) {
        validationGoal
                .checkGoalStatusForCompleted(goalRepository.findGoalById(goalId).getStatus()
                        .equals(GoalStatus.COMPLETED));

        validationGoal
                .checkSizeSkillIdsDontEqualsExistingSkillsInDB(
                        goalDto.getSkillIds().size() !=
                                skillService.checkAmountSkillsInDB(goalDto.getSkillIds())
                );

        if (goalDto.getStatus().equals(GoalStatus.COMPLETED.toString())) {
            List<User> users = goalRepository.findUsersByGoalId(goalId);
            users.forEach(user ->
                    goalDto.getSkillIds()
                            .forEach(skill ->
                                    skillService.assignSkillToUser(skill, user.getId())));

            goalRepository.updateGoalStatusById(goalId);
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
}
