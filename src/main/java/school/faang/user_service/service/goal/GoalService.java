package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exceptions.GoalOverflowException;
import school.faang.user_service.exceptions.SkillNotFound;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;

    public void deleteGoal(long goalID) {
        goalRepository.deleteById(goalID);
    }

    public void createGoal(Long userId, Goal goal) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found"));

        boolean isExistingSkill = goal.getSkillsToAchieve().stream()
                .map(Skill::getId)
                .allMatch(skillService::checkActiveSkill);

        if (user.getGoals().size() < 3 && isExistingSkill) {
            goalRepository.create(goal.getTitle(), goal.getDescription(), goal.getParent().getId());

            List<Skill> skills = goal.getSkillsToAchieve().stream()
                    .peek(skill -> skill.getGoals().add(goal))
                    .toList();
            skillService.saveAll(skills);

        } else if (user.getGoals().size() >= 3) {
            throw new GoalOverflowException("Maximum goal limit exceeded. Only 3 goals are allowed.");
        } else {
            throw new SkillNotFound("Skill not exist");
        }
    }
}