package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalServiceValidator;
import school.faang.user_service.validator.SkillServiceValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalInvitationService goalInvitationService;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final GoalServiceValidator goalServiceValidator;
    private final SkillServiceValidator skillServiceValidator;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        int userGoalsCount = goalRepository.countActiveGoalsPerUser(userId);

        goalServiceValidator.validateUserGoalLimit(userGoalsCount);
        goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());

        if (goalDto.getSkillIds() != null && !goalDto.getSkillIds().isEmpty()) {
            skillService.create(goalMapper.toGoal(goalDto).getSkillsToAchieve(), userId);
        }

        return goalDto;
    }

    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal with this id does not exist in the database"));

        Goal updatedGoal = prepareAndValidateGoalUpdate(goal, goalDto);
        goalRepository.save(updatedGoal);

        List<User> users = goalRepository.findUsersByGoalId(goalId);
        skillService.addSkillToUsers(users, goalId);

        return goalMapper.toGoalDto(updatedGoal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        Stream<Goal> goal = goalRepository.findByParent(goalId);
        goalServiceValidator.validateGoalsExist(goal);

        goalRepository.deleteById(goalId);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(long goalId,
                                              String filterTitle,
                                              String filterDescription,
                                              GoalStatus filterStatus,
                                              List<Long> filterSkills) {
        Stream<Goal> goals = goalRepository.findByParent(goalId);
        GoalFilterDto filterDto = new GoalFilterDto(filterTitle, filterDescription, filterStatus, filterSkills);

        return filterGoals(goals, filterDto);
    }

    @Transactional
    public List<GoalDto> getGoalsByUser(long userId,
                                        String filterTitle,
                                        String filterDescription,
                                        GoalStatus filterStatus,
                                        List<Long> filterSkills) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);
        GoalFilterDto filterDto = new GoalFilterDto(filterTitle, filterDescription, filterStatus, filterSkills);

        return filterGoals(goals, filterDto);
    }

    private Goal prepareAndValidateGoalUpdate(Goal existingGoal, GoalDto goalDto) {
        Goal updateGoal = goalMapper.toGoal(goalDto);

        goalServiceValidator.validateGoalStatusNotCompleted(existingGoal);
        skillServiceValidator.validateExistByTitle(updateGoal.getSkillsToAchieve());

        existingGoal.setStatus(updateGoal.getStatus());
        existingGoal.setTitle(updateGoal.getTitle());
        existingGoal.setSkillsToAchieve(updateGoal.getSkillsToAchieve());
        existingGoal.setDescription(updateGoal.getDescription());

        return existingGoal;
    }

    private List<GoalDto> filterGoals(Stream<Goal> goals, GoalFilterDto filterDto) {
        if (filterDto == null) {
            return goals.map(goalMapper::toGoalDto).toList();
        } else {
            return goalFilters.stream()
                    .filter(filter -> filter.isApplicable(filterDto))
                    .reduce(goals, (currentGoals, filter) -> filter.apply(currentGoals, filterDto), (s1, s2) -> s1)
                    .map(goalMapper::toGoalDto)
                    .toList();
        }
    }

    public void deactivateActiveUserGoals(User user) {
        user.getGoals().stream()
                .filter(goal -> goal.getStatus().equals(GoalStatus.ACTIVE))
                .forEach(goal -> {
                    List<GoalInvitation> goalInvitations = goal.getInvitations();

                    goal.getUsers().remove(user);
                    if (goal.getUsers().isEmpty()) {
                        goalInvitationService.deleteGoalInvitations(goalInvitations);
                        goalRepository.deleteById(goal.getId());
                    } else {
                        goalInvitationService.deleteGoalInvitationForUser(goalInvitations, user);
                    }
                });

        user.getGoals().clear();
    }
}