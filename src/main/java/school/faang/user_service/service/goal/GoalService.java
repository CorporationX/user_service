package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.ErrorCode;
import school.faang.user_service.exception.ValidationException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalService {
    private static final int MAX_ACTIVE_GOALS = 3;
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        validateGoal(userId, goalDto);
        Goal createdGoal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());
        addSkillsToGoal(goalDto.getSkillIds(), createdGoal.getId());
        return goalMapper.toDto(createdGoal);
    }

    @Transactional
    public GoalDto updateGoal(Long userId, GoalDto goalDto) {
        validateGoal(userId, goalDto);
        Goal goalToUpdate = findGoal(goalDto.getId());
        if (GoalStatus.COMPLETED.equals(goalToUpdate.getStatus()) && !goalToUpdate.getSkillsToAchieve().isEmpty()) {
            assignSkillsToUsers(goalToUpdate, goalDto);
            updateSkills(goalDto);
        }
        return goalMapper.toDto(goalToUpdate);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(Long goalId) {
        List<Goal> goals = goalRepository.findByParent(goalId).toList();
        return goals.stream()
                .map(goalMapper::toDto)
                .toList();
    }

    @Transactional
    public List<GoalDto> getGoalsByUserId(Long userId, GoalFilterDto filter) {
        Stream<Goal> userGoals = goalRepository.findGoalsByUserId(userId);
        return filterGoals(userGoals, filter);
    }

    private void assignSkillsToUsers(Goal goal, GoalDto goalDto) {
        goal.getUsers().forEach(user -> goalDto.getSkillIds().forEach(skillId ->
                skillRepository.assignSkillToUser(skillId, user.getId())));
    }

    private void validateGoal(Long userId, GoalDto goalDto) {
        if (goalDto.getTitle() == null || goalDto.getTitle().isBlank()) {
            throw new ValidationException(ErrorCode.GOAL_EMPTY_TITLE, String.valueOf(goalDto.getId()));
        }
        long countActiveGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (countActiveGoals > MAX_ACTIVE_GOALS) {
            throw new ValidationException(ErrorCode.MAX_ACTIVE_GOALS,
                    String.valueOf(countActiveGoals));
        }
        List<Skill> existingSkills = skillRepository.findAllById(goalDto.getSkillIds());

        Set<Long> nonExistingSkillIds = goalDto.getSkillIds().stream()
                .filter(skillId -> existingSkills.stream()
                        .noneMatch(skill -> skillId.equals(skill.getId())))
                .collect(Collectors.toSet());

        if (!nonExistingSkillIds.isEmpty()) {
            throw new ValidationException(ErrorCode.GOAL_NON_EXISTING_SKILLS, nonExistingSkillIds.toString());
        }
    }

    private Goal findGoal(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new ValidationException(ErrorCode.GOAL_NOT_FOUND, goalId.toString()));
    }

    private void addSkillsToGoal(List<Long> skillIds, Long goalId) {
        Goal goalForSkills = findGoal(goalId);
        goalForSkills.getSkillsToAchieve().addAll(skillRepository.findAllById(skillIds));
        goalRepository.save(goalForSkills);
    }

    private void updateSkills(GoalDto goalDto) {
        goalRepository.removeSkillsFromGoal(goalDto.getId());
        addSkillsToGoal(goalDto.getSkillIds(), goalDto.getId());
    }

    private List<GoalDto> filterGoals(Stream<Goal> userGoals, GoalFilterDto filter) {
        for (GoalFilter goalFilter : goalFilters) {
            if (goalFilter.isApplicable(filter)) {
                userGoals = goalFilter.apply(userGoals, filter);
            }
        }
        return userGoals
                .map(goalMapper::toDto)
                .toList();
    }
}
