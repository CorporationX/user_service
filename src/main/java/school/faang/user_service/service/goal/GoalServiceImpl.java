package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.event.GoalCompletedEvent;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;
import school.faang.user_service.repository.goal.GoalRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final GoalCompletedEventPublisher goalCompletedEventPublisher;
    private final GoalMapper goalMapper;

    @Transactional
    public GoalDto completeGoal(Long goalId, Long userId) {
        log.info("Completing goal with id: {}", goalId);

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new DataValidationException("Goal not found with id: " + goalId));

        goal.setStatus(GoalStatus.COMPLETED);
        LocalDateTime completedAt = LocalDateTime.now();
        Goal savedGoal = goalRepository.save(goal);

        goalCompletedEventPublisher.publish(new GoalCompletedEvent(userId, goalId, completedAt));

        log.info("Goal completed successfully: goalId={}, userId={}", goalId, userId);

        return goalMapper.toGoalDto(savedGoal);
    }
}
