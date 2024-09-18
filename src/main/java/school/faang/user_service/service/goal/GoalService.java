package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.validator.goal.GoalValidator;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final GoalValidator goalValidator;
    private final SkillRepository skillRepository;
    private final SkillValidator skillValidator;
    private final UserService userService;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        User user = userService.getUserById(userId);
        goalValidator.validateUserGoalLimit(userId);

        Goal goal = createGoalEntity(goalDto, user);

        return goalMapper.toGoalDto(goal);
    }

    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {
        Goal existingGoal = getGoalById(goalId);

        Goal updatedGoal = updateGoalEntity(existingGoal, goalDto);

        return goalMapper.toGoalDto(updatedGoal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findByParent(goalId);

        return filterGoals(goals, filterDto);
    }

    @Transactional
    public List<GoalDto> getGoalsByUser(long userId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findGoalsByUserId(userId);

        return filterGoals(goals, filterDto);
    }

    private Goal getGoalById(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal with this id does not exist in the database"));
    }

    private Goal createGoalEntity(GoalDto goalDto, User user) {
        Goal goal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());

        goal.getUsers().add(user);
        setSkillsToGoal(goalDto.getSkillIds(), goal);

        goalRepository.save(goal);

        return goal;
    }

    private Goal updateGoalEntity(Goal existingGoal, GoalDto goalDto) {
        goalValidator.validateGoalStatusNotCompleted(existingGoal);

        existingGoal.setTitle(goalDto.getTitle());
        existingGoal.setDescription(goalDto.getDescription());

        if (goalDto.getStatus() != null) {
            existingGoal.setStatus(goalDto.getStatus());
        }

        if (goalDto.getParentId() != null) {
            goalRepository.findById(goalDto.getParentId()).ifPresent(existingGoal::setParent);
        }

        setSkillsToGoal(goalDto.getSkillIds(), existingGoal);

        goalRepository.save(existingGoal);

        return existingGoal;
    }

    private void setSkillsToGoal(List<Long> skillIds, Goal goal) {
        if (skillIds != null) {
            skillValidator.validateAllSkillsIdsExist(skillIds);
            List<Skill> skills = skillRepository.findAllById(skillIds);
            goal.setSkillsToAchieve(skills);
        }
    }

    private List<GoalDto> filterGoals(Stream<Goal> goals, GoalFilterDto filterDto) {
        return goalFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(goals, (currentGoals, filter) -> filter.apply(currentGoals, filterDto), (s1, s2) -> s1)
                .map(goalMapper::toGoalDto)
                .toList();
    }
}


