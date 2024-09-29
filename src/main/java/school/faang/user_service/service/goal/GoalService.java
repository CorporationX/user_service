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
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private static final int MAX_ACTIVE_GOALS_PER_USER = 3;

    private final GoalMapper goalMapper;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final GoalFilterService goalFilterService;

    @Transactional
    public void createGoal(Long userId, GoalDto goalDto) {
        Goal goal = goalMapper.goalDtoToGoal(goalDto);
        setCreatorAsUser(userId, goal);
        checkMaxActiveGoals(userId);
        setSkillsToGoal(goalDto.skillIds(), goal);
        goal.setStatus(GoalStatus.ACTIVE);
        goalRepository.save(goal);
    }


    private void checkMaxActiveGoals(Long userId) {
        int activeGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoals >= MAX_ACTIVE_GOALS_PER_USER) {
            throw new IllegalArgumentException("Пользователь не может иметь более " + MAX_ACTIVE_GOALS_PER_USER + " активных целей");
        }
    }

    private void setSkillsToGoal(List<Long> skillIds, Goal goal) {
        if (skillIds == null || skillIds.isEmpty()) {
            throw new IllegalArgumentException("Навыки не могут быть пустыми");
        }

        List<Skill> skills = skillRepository.findAllById(skillIds);
        if (skills.size() != skillIds.size()) {
            throw new IllegalArgumentException("Некоторые навыки не существуют");
        }
        goal.setSkillsToAchieve(skills);
    }

    private void setCreatorAsUser(Long userId, Goal goal) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        goal.setUsers(Collections.singletonList(creator));
    }

    @Transactional
    public void updateGoal(Long goalId, GoalDto goalDto) {
        Goal existingGoal = getExistingGoal(goalId);
        checkGoalNotCompleted(existingGoal);
        updateGoalFields(existingGoal, goalDto);
        handleStatusChange(existingGoal, goalDto);
        updateSkills(existingGoal, goalDto.skillIds());
        goalRepository.save(existingGoal);
    }

    private Goal getExistingGoal(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Цель не найдена"));
    }

    private void checkGoalNotCompleted(Goal existingGoal) {
        if (existingGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("Нельзя обновлять завершенную цель");
        }
    }

    private void updateGoalFields(Goal existingGoal, GoalDto goalDto) {
        if (goalDto.title() != null) {
            existingGoal.setTitle(goalDto.title());
        }
        if (goalDto.description() != null) {
            existingGoal.setDescription(goalDto.description());
        }
        if (goalDto.deadline() != null) {
            existingGoal.setDeadline(goalDto.deadline());
        }
    }

    private void handleStatusChange(Goal existingGoal, GoalDto goalDto) {
        if (goalDto.status() == GoalStatus.COMPLETED && existingGoal.getStatus() != GoalStatus.COMPLETED) {
            existingGoal.setStatus(GoalStatus.COMPLETED);
            assignSkillsToUsers(existingGoal);
        } else if (goalDto.status() != null) {
            existingGoal.setStatus(goalDto.status());
        }
    }

    private void assignSkillsToUsers(Goal existingGoal) {
        List<User> users = goalRepository.findUsersByGoalId(existingGoal.getId());
        List<Skill> skills = existingGoal.getSkillsToAchieve();
        users.forEach(user -> user.getSkills().addAll(skills));
        userRepository.saveAll(users);
    }

    private void updateSkills(Goal existingGoal, List<Long> skillIds) {
        if (skillIds != null) {
            setSkillsToGoal(skillIds, existingGoal);
        }
    }

    @Transactional
    public void deleteGoal(long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Цель не найдена"));
        goalRepository.delete(goal);
    }

    @Transactional(readOnly = true)
    public List<GoalDto> findSubtasksByGoalId(long goalId, String titleFilter) {
        List<Goal> subtasks = goalRepository.findByParent(goalId,titleFilter);

        if (titleFilter != null && !titleFilter.isEmpty()) {
            subtasks = subtasks.stream()
                    .filter(goal -> goal.getTitle().contains(titleFilter))
                    .toList();
        }

        return subtasks.stream()
                .map(goalMapper::goalToGoalDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GoalDto> findGoalsByUserId(Long userId, GoalFilterDto filter) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId);

        if (goals.isEmpty()) {
            System.out.println("Нет целей для пользователя: " + userId);
            return Collections.emptyList();
        }

        Stream<Goal> goalStream = goals.stream();

        if (filter != null) {
            goalStream = goalFilterService.applyFilters(goalStream, filter);
        }

        List<GoalDto> result = goalStream
                .map(goalMapper::goalToGoalDto)
                .collect(Collectors.toList());

        return result;
    }
}