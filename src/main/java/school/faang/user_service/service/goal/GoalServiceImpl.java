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
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.exception.NotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.publisher.CompletedGoalPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.GoalValidator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final List<GoalFilter> goalFilters;
    private final CompletedGoalPublisher completedGoalPublisher;

    @Override
    @Transactional
    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto goalFilterDto) {
        goalValidator.validateGoalId(userId);

        Stream<Goal> goalStream = goalRepository.findGoalsByUserId(userId);

        return applyFilters(goalStream, goalFilterDto);
    }

    @Override
    @Transactional
    public List<GoalDto> getSubtasksByGoalId(long goalId, GoalFilterDto filter) {
        goalValidator.validateGoalId(goalId);

        Stream<Goal> goalStream = goalRepository.findByParent(goalId);

        return applyFilters(goalStream, filter);
    }

    @Override
    @Transactional
    public Goal findGoalById(long id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Goal with id %s not found", id)));
    }

    @Override
    public int findActiveGoalsByUserId(long id) {
        return goalRepository.countActiveGoalsPerUser(id);
    }

    @Override
    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id: " + userId + " not found"));

        goalValidator.validateUserGoalsAmount(user);
        goalValidator.validateAllSkillsExist(goalDto);

        Goal goal = goalMapper.toEntity(goalDto);
        goalMapper.convertDtoIdsToEntity(goalDto, goal);
        goal.setUsers(Collections.singletonList(user));
        goal = goalRepository.save(goal);

        return goalMapper.toDto(goal);
    }

    @Override
    @Transactional
    public GoalDto updateGoal(long userId, Long goalId, GoalDto goalDto) {
        Goal goalToUpdate = goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal with id: " + goalId + " not found"));

        goalValidator.validateGoalNotCompleted(goalToUpdate);

        Goal updatedGoal = goalMapper.toEntity(goalDto);
        goalMapper.update(goalDto, updatedGoal);
        goalMapper.convertDtoIdsToEntity(goalDto, updatedGoal);
        assignSkills(goalToUpdate);

        if (updatedGoal.getStatus().equals(GoalStatus.COMPLETED)) {
            completedGoalPublisher.publish(new GoalCompletedEvent(userId, updatedGoal.getId(), LocalDateTime.now()));
        }

        goalRepository.save(updatedGoal);
        return goalDto;
    }

    @Override
    @Transactional
    public GoalDto deleteGoal(long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal with id: " + goalId + " not found"));

        List<Goal> subtasks = goalRepository.findByParent(goalId)
                .toList();

        goalRepository.deleteAll(subtasks);
        goalRepository.delete(goal);

        return goalMapper.toDto(goal);
    }

    @Override
    @Transactional
    public void delete(Goal goal) {
        goalRepository.delete(goal);
    }

    private List<GoalDto> applyFilters(Stream<Goal> goalStream, GoalFilterDto filter) {

        if (filter == null) {
            return goalStream.map(goalMapper::toDto).toList();
        }

        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isAcceptable(filter)) {
                goalStream = goalFilter.applyFilter(goalStream, filter);
            }
        }

        return goalStream.map(goalMapper::toDto).toList();
    }

    private void assignSkills(Goal goalToUpdate) {

        List<Skill> skills = goalToUpdate.getSkillsToAchieve();
        List<User> usersCompletedGoal = goalToUpdate.getUsers();

        if (skills == null || usersCompletedGoal == null) {
            return;
        }

        for (User user : usersCompletedGoal) {
            if (user != null) {
                skills.forEach(skill -> skillRepository.assignSkillToUser(skill.getId(), user.getId()));
            }
        }
    }
}
