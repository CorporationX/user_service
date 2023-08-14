package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.invalidFieldException.DataValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filter) {
        GoalValidator.validateId(userId, "User");

        Stream<Goal> goalStream = goalRepository.findGoalsByUserId(userId);

        return checkExistFilterAndApplyFilters(goalStream, filter);
    }

    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filter) {
        GoalValidator.validateId(goalId, "Goal");

        Stream<Goal> goalStream = goalRepository.findByParent(goalId);

        return checkExistFilterAndApplyFilters(goalStream, filter);
    }

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        User user = validateAndGetUser(userId, goalDto);

        Goal goal = goalMapper.toEntity(goalDto);
        goalMapper.convertDtoDependenciesToEntity(goalDto, goal);
        goal.setUsers(new ArrayList<>());
        goal.getUsers().add(user);

        return goalMapper.toDto(goalRepository.save(goal));
    }

    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalToUpdate = validateAndGetGoal(goalId, goalDto);

        goalMapper.update(goalDto, goalToUpdate);
        goalMapper.convertDtoDependenciesToEntity(goalDto, goalToUpdate);
        checkGoalCompletionAndAssignmentSkills(goalToUpdate, goalDto);

        return goalMapper.toDto(goalRepository.save(goalToUpdate));
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        GoalValidator.validateId(goalId, "Goal");
        if (!goalRepository.existsById(goalId))
            throw new DataValidationException("Goal with given id was not found!");
        goalRepository.deleteById(goalId);
    }
    @Transactional
    public void save(Goal goal) {
        goalRepository.save(goal);
    }


    private List<GoalDto> checkExistFilterAndApplyFilters(Stream<Goal> goalStream, GoalFilterDto filter) {
        if (filter == null) {
            return goalStream.map(goalMapper::toDto).toList();
        }

        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(filter)) {
                goalStream = goalFilter.apply(goalStream, filter);
            }
        }

        return goalStream.map(goalMapper::toDto).toList();
    }

    private void checkGoalCompletionAndAssignmentSkills(Goal goalToUpdate, GoalDto goalDto) {
        if (goalToUpdate.getStatus() == GoalStatus.ACTIVE && goalDto.getStatus() != null && goalDto.getStatus() == GoalStatus.COMPLETED) {
            List<Skill> skills = goalToUpdate.getSkillsToAchieve();
            if (skills == null) {
                return;
            }
            List<User> usersCompletedGoal = goalToUpdate.getUsers();

            usersCompletedGoal.forEach(user -> {
                skills.forEach(skill -> skillRepository.assignSkillToUser(skill.getId(), user.getId()));
            });
        }
    }

    private Goal validateAndGetGoal(Long goalId, GoalDto goalDto) {
        GoalValidator.validateId(goalId, "Goal");
        GoalValidator.validateGoal(goalDto);
        Goal goalToUpdate = goalRepository.findById(goalId)
                .orElseThrow(() -> new DataValidationException("Goal with given id was not found!"));
        GoalValidator.validateUpdatingGoal(goalToUpdate);
        return goalToUpdate;
    }

    private User validateAndGetUser(Long userId, GoalDto goalDto) {
        GoalValidator.validateId(userId, "User");
        GoalValidator.validateGoal(goalDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User with given id was not found!"));
        GoalValidator.validateAdditionGoalToUser(user, goalDto);
        return user;
    }
}
