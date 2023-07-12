package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;

    public void updateGoal(Goal goalFromUpdate) {
        List<Skill> skillsToAchieve = goalFromUpdate.getSkillsToAchieve();
        List<User> users = goalFromUpdate.getUsers();
        Goal goalToUpdate = goalRepository.findById(goalFromUpdate.getId())
                .orElseThrow(() -> new IllegalArgumentException("Goal not found"));

        validateUpdate(goalToUpdate, goalFromUpdate, skillsToAchieve);

        if (goalFromUpdate.getStatus().equals(GoalStatus.COMPLETED)) {
            skillsToAchieve.forEach(skill -> {
                users.forEach(user -> {
                    if (!user.getSkills().contains(skill)) {
                        skillRepository.assignSkillToUser(skill.getId(), user.getId());
                    }
                });
            });
            goalRepository.delete(goalToUpdate);
        } else {
            goalRepository.save(goalFromUpdate);
        }
    }

    private void validateUpdate(Goal goalToUpdate, Goal goalFromUpdate, List<Skill> skillsToAchieve) {
        if (goalFromUpdate.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }

        if (goalToUpdate.getStatus().equals(GoalStatus.COMPLETED)) {
            throw new IllegalArgumentException("Goal already completed");
        }

        skillsToAchieve.forEach(skillToAchieve -> {
            if (!skillRepository.existsByTitle(skillToAchieve.getTitle())) {
                throw new IllegalArgumentException("Skill " + skillToAchieve.getTitle() + " not found");
            }
        });
    }
}
