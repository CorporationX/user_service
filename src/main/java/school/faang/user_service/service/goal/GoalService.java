package school.faang.user_service.service.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Transactional
    public void createGoal(Long userId, Goal goal) {
        if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Goal must have a title");
        }

        int activeGoals = goalRepository.countActiveGoalsPerUser(userId);
        if (activeGoals >= 3) {
            throw new IllegalArgumentException("User can't have more than 3 active goals");
        }

        for (Skill skill : goal.getSkillsToAchieve()) {
            if (!skillRepository.existsById(skill.getId())) {
                throw new IllegalArgumentException("Skill with ID " + skill.getId() + " does not exist");
            }
        }

        goalRepository.save(goal);
    }

    @Transactional
    public void updateGoal(Long goalId, GoalDto goalDto) {
        Goal existingGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (goalDto.getTitle() == null || goalDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Goal must have a title");
        }

        if (existingGoal.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("Completed goal cannot be modified");
        }

        validateSkills(goalDto.getSkillIds());

        existingGoal.setTitle(goalDto.getTitle());
        existingGoal.setDescription(goalDto.getDescription());
        existingGoal.setStatus(goalDto.getGoalStatus());

        updateGoalSkills(existingGoal, goalDto.getSkillIds());

        if (goalDto.getGoalStatus() == GoalStatus.COMPLETED) {
            assignSkillsToParticipants(existingGoal);
        }

        goalRepository.save(existingGoal);
    }

    private void validateSkills(List<Long> skillIds) {
        if (skillIds != null && !skillIds.isEmpty()) {
            for (Long skillId : skillIds) {
                if (!skillRepository.existsById(skillId)) {
                    throw new IllegalArgumentException("Skill with ID " + skillId + " does not exist");
                }
            }
        }
    }

    private void updateGoalSkills(Goal goal, List<Long> newSkillIds) {
        goal.getSkillsToAchieve().clear();

        if (newSkillIds != null && !newSkillIds.isEmpty()) {
            for (Long skillId : newSkillIds) {
                Skill skill = skillRepository.findById(skillId)
                        .orElseThrow(() -> new IllegalArgumentException("Skill with ID " + skillId + " not found"));
                goal.getSkillsToAchieve().add(skill);
            }
        }
    }

    private void assignSkillsToParticipants(Goal goal) {
        List<User> participants = goalRepository.findUsersByGoalId(goal.getId());
        for (User participant : participants) {
            for (Skill skill : goal.getSkillsToAchieve()) {
                skillRepository.assignSkillToUser(skill.getId(), participant.getId());
            }
        }
    }

    @Transactional
    public void deleteGoal(long goalId) {
        Goal goal = goalRepository.findById(goalId).orElseThrow(
                () -> new IllegalArgumentException("Goal not found")
        );
        goal.getSkillsToAchieve().clear();
        goalRepository.save(goal);

        goalRepository.deleteById(goalId);
    }

    public List<GoalDto> findSubtasksByGoalIdWithFilter(Long goalId, GoalFilterDto filter) {
        List<Goal> subtasks = goalRepository.findByParent(goalId).collect(Collectors.toList());

        if (subtasks.isEmpty()) {
            throw new NoSuchElementException("Subtasks not found for goal with ID: " + goalId);
        }

        if (filter != null) {
            if (filter.getStatus() != null) {
                subtasks = subtasks.stream()
                        .filter(goal -> goal.getStatus().equals(filter.getStatus()))
                        .collect(Collectors.toList());
            }
        }

        return subtasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private GoalDto convertToDto(Goal goal) {
        return GoalDto.builder()
                .id(goal.getId())
                .parentId(goal.getParent() != null ? goal.getParent().getId() : null)
                .goalStatus(goal.getStatus())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .skillIds(goal.getSkillsToAchieve().stream()
                        .map(Skill::getId)
                        .collect(Collectors.toList()))
                .build();
    }

    public List<GoalDto> findGoalsByUserIdWithFilter(long userId, GoalFilterDto filter) {
        List<Goal> goals = goalRepository.findGoalsByUserId(userId)
                .collect(Collectors.toList());

        if (filter != null) {
            if (filter.getTitle() != null) {
                goals = goals.stream()
                        .filter(goal -> goal.getTitle().contains(filter.getTitle()))
                        .collect(Collectors.toList());
            }
            if (filter.getStatus() != null) {
                goals = goals.stream()
                        .filter(goal -> goal.getStatus().equals(filter.getStatus()))
                        .collect(Collectors.toList());
            }
        }

        return goals.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}