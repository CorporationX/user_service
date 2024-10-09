package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.GoalCompletedEventDto;
import school.faang.user_service.publisher.GoalCompletedEventPublisher;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalCompletedEventPublisher goalCompletedPublisher;

    @Override
    public void completeUserGoal(long userId, long goalId) {
        // TODO: реализовать метод перед отправкой  в прод
        GoalCompletedEventDto goalCompletedEventDto = new GoalCompletedEventDto(userId, goalId, LocalDateTime.now());
        goalCompletedPublisher.publish(goalCompletedEventDto);
    }
}