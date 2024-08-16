package school.faang.user_service.validator.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

@Component
public class GoalValidator {

    private final GoalRepository goalRepository;
    private final SkillService skillService;

    @Autowired
    public GoalValidator(GoalRepository goalRepository, SkillService skillService) {
        this.goalRepository = goalRepository;
        this.skillService = skillService;
    }

    public void createGoalValidator(Long userId, GoalDto goalDto) {
        if (goalDto.getTitle() == null || goalDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Goal must have a title");
        }

        if (goalRepository.countActiveGoalsPerUser(userId) >= 3) {
            throw new IllegalArgumentException("User cannot have more than 3 active goals");
        }

        if (skillService.countExisting(goalDto.getSkillIds()) != goalDto.getSkillIds().size()) {
            throw new IllegalArgumentException("One or more skills do not exist.");
        }
    }

    public void updateGoalValidator(Long goalId, GoalDto goalDto) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        if (goal.getStatus() == GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot update a completed goal");
        }

        if (goalDto.getTitle() == null || goalDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Goal must have a title");
        }

        if (skillService.countExisting(goalDto.getSkillIds()) != goalDto.getSkillIds().size()) {
            throw new IllegalArgumentException("One or more skills do not exist");
        }
    }

    public void deleteGoalValidator(Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));
    }
}
