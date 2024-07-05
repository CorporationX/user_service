package school.faang.user_service.service.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Service
@Transactional
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillService skillService;

    @Autowired
    public GoalService(GoalRepository goalRepository, SkillService skillService) {
        this.goalRepository = goalRepository;
        this.skillService = skillService;
    }

    public Goal createGoal(Long userId, Goal goal) {
        if (goal.getTitle() == null || goal.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Goal must have a title");
        }

        if (goalRepository.countActiveGoalsPerUser(userId) >= 3) {
            throw new IllegalArgumentException("User cannot have more than 3 active goals");
        }

        List<Long> skillIds = goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList();
        if (skillService.countExisting(skillIds) != skillIds.size()) {
            throw new IllegalArgumentException("One or more skills do not exist.");
        }

        Goal savedGoal = goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());

        skillIds.forEach(skillId -> goalRepository.addSkillToGoal(savedGoal.getId(), skillId));
        return savedGoal;
    }

    public Goal updateGoal(Long goalId, GoalDto goalDto) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot update a completed goal");
        }

        if (goalDto.getTitle() == null || goalDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Goal must have a title");
        }

        List<Long> skillIds = goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .toList();

        if (skillService.countExisting(skillIds) != skillIds.size()) {
            throw new IllegalArgumentException("One or more skills do not exist");
        }

        goal.setTitle(goalDto.getTitle());
        goal.setDescription(goalDto.getDescription());
        goal.setStatus(goalDto.getStatus());

        Goal savedGoal = goalRepository.save(goal);

        goalRepository.removeSkillsFromGoal(goalId);
        skillIds.forEach(skillId -> goalRepository.addSkillToGoal(savedGoal.getId(), skillId));

        if (goalDto.getStatus() == GoalStatus.COMPLETED) {
            List<User> users = goalRepository.findUsersByGoalId(goalId);
            users.forEach(user -> skillIds.forEach(skillId -> skillService.assignSkillToUser(skillId, user.getId())));
        }

        return savedGoal;
    }

    public void deleteGoal(long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        goalRepository.removeSkillsFromGoal(goalId);
        goalRepository.deleteById(goalId);
    }
}
