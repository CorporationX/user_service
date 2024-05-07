package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validator.GoalValidator;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {


    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final SkillService skillService;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;


    @Override
    @Transactional
    public List<GoalDto> findGoalsByUserId(long userId, GoalFilterDto goalFilterDto) {
        goalValidator.validateThatIdIsGreaterThan0(userId);

        List<GoalDto> goals = goalRepository
                .findGoalsByUserId(userId)
                .map(goalMapper::toDto)
                .toList();

        return goalValidator.validateGoalsByUserIdAndSort(goals, goalFilterDto);
    }

    @Override
    @Transactional
    public GoalDto createGoal(Long userId, Goal goal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        goalValidator.validateGoalCreation(user, goal);

        List<Skill> skillsToAchieve = goal.getSkillsToAchieve();

        skillsToAchieve.forEach(skill -> skill.getGoals().add(goal));

        skillService.saveAll(skillsToAchieve);

        return goalMapper.toDto(goalRepository.save(goal));
    }

    @Override
    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        Goal goalToUpdate = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found"));

        goalValidator.validateGoalUpdate(goalToUpdate);

        goalMapper.update(goalDto, goalToUpdate);

        return goalMapper.toDto(goalRepository.save(goalToUpdate));
    }

    @Override
    @Transactional
    public void deleteGoal(long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal with id: " + goalId + " not found"));

        goalRepository.delete(goal);
    }

    @Override
    @Transactional
    public List<GoalDto> findSubtasksByGoalId(long goalId) {
        goalValidator.validateThatIdIsGreaterThan0(goalId);

        List<Goal> subtasks = goalRepository.findByParent(goalId)
                .toList();

        goalValidator.validateFindSubtasks(subtasks, goalId);

        return subtasks.stream()
                .sorted(Comparator.comparing(Goal::getId))
                .map(goalMapper::toDto)
                .collect(Collectors.toList());
    }
}
