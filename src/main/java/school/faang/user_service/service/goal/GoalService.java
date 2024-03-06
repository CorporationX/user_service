package school.faang.user_service.service.goal;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.filter.GoalFilter;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final GoalValidator goalValidator;
    private final SkillRepository skillRepository;
    private final List<GoalFilter> goalFilters;
    private static final int MAX_USER_ACTIVE_GOALS = 3;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        goalValidator.validateGoalCreation(userId, goalDto, MAX_USER_ACTIVE_GOALS);

        Goal createdGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        createdGoal.setSkillsToAchieve(skillIdsToSkills(goalDto.getSkillIds()));
        goalRepository.assignGoalToUser(createdGoal.getId(), userId);

        return goalMapper.toDto(goalRepository.save(createdGoal));
    }

    @Transactional
    public GoalDto updateGoal(Long goalId, GoalDto goalDto) {
        goalValidator.validateGoalUpdate(goalId, goalDto);

        Goal goal = goalRepository.findById(goalId).get();
        updateGoalFields(goal, goalDto);

        return goalMapper.toDto(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        goalValidator.validateGoalExists(goalId);
        goalRepository.deleteById(goalId);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(Long goalId, GoalFilterDto filters) {
        goalValidator.validateGoalExists(goalId);
        List<Goal> goals = goalRepository.findByParent(goalId)
                .collect(Collectors.toList());
        applyFilters(goals, filters);

        return goalMapper.toDto(goals);
    }

    @Transactional
    public List<GoalDto> getGoalsByUser(Long userId, GoalFilterDto filters) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId)
                .collect(Collectors.toList());
        applyFilters(goals, filters);

        return goalMapper.toDto(goals);
    }

    private void applyFilters(List<Goal> goals, GoalFilterDto filters) {
        goalFilters.stream()
                .filter(goalFilter -> goalFilter.isApplicable(filters))
                .forEach(goalFilter -> goalFilter.apply(goals, filters));
    }

    private void updateGoalFields(Goal goal, GoalDto goalDto) {
        if (goalDto.getParentId() != null) {
            Long parentId = goalDto.getParentId();
            goalValidator.validateGoalExists(parentId);
            goal.setParent(goalRepository.findById(parentId).get());
        }
        if (goalDto.getStatus().equals(GoalStatus.COMPLETED)) {
            goal.getUsers().forEach(user -> addSkillsToUser(user, goal));
        }
        goal.setTitle(goalDto.getTitle());
        goal.setStatus(goalDto.getStatus());
        goal.setDescription(goalDto.getDescription());
        goal.setSkillsToAchieve(skillIdsToSkills(goalDto.getSkillIds()));
    }

    private void addSkillsToUser(User user, Goal goal) {
        goal.getSkillsToAchieve().forEach(skill -> {
            if (!user.getSkills().contains(skill)) {
                skillRepository.assignSkillToUser(skill.getId(), user.getId());
            }
        });
    }

    private List<Skill> skillIdsToSkills(List<Long> skillIds) {
        return skillRepository.findAllById(skillIds);
    }
}
