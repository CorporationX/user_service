package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.GoalCompletedEventDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final SkillServiceImpl skillService;
    private final GoalCompletedEventPublisher goalCompletedPublisher;
    private final SkillRepository skillRepository;

    @Override
    public void completeUserGoal(long userId, long goalId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new IllegalArgumentException("Goal not found with id: " + goalId));

        validateUserGoal(user, goal);
        updateSkills(user, goalId);

        GoalCompletedEventDto goalCompletedEventDto = new GoalCompletedEventDto(userId, goalId, LocalDateTime.now());
        goalCompletedPublisher.publish(goalCompletedEventDto);
    }

    private void validateUserGoal(User user, Goal goal) {
        if (!user.getGoals().contains(goal)){
            throw new IllegalArgumentException("User doesn't have the goal");
        }
        if (goal.getStatus() != GoalStatus.COMPLETED){
            throw new IllegalArgumentException("Goal is not completed");
        }
    }

    private void updateSkills(User user, long goalId) {
        List<Skill> oldSkills = user.getSkills();
        List<Skill> newSkills = skillRepository.findSkillsByGoalId(goalId);
        skillService.removeSkillsFromUser(user, oldSkills);
        skillService.assignSkillsToUser(user, newSkills);
    }
}