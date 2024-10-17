package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.GoalCompletedEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private final SkillService skillService;
    private final GoalCompletedEventPublisher goalCompletedPublisher;

    @Override
    public void completeUserGoal(long userId, long goalId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found with id: " + goalId));

        validateUserGoal(user, goal);
        skillService.updateSkills(user, goal.getId());

        GoalCompletedEventDto goalCompletedEventDto = new GoalCompletedEventDto(userId, goalId, LocalDateTime.now());
        goalCompletedPublisher.publish(goalCompletedEventDto);
    }

    private void validateUserGoal(User user, Goal goal) {
        if (!user.getGoals().contains(goal)) {
            throw new IllegalArgumentException("User doesn't have the goal");
        }
        if (goal.getStatus() != GoalStatus.COMPLETED) {
            throw new IllegalArgumentException("Goal is not completed");
        }
    }
}