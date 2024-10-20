package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.redis.GoalCompletedDto;
import school.faang.user_service.publisher.EventPublisher;
import school.faang.user_service.publisher.RedisTopics;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goal")
public class GoalController {
    private final EventPublisher eventPublisher;

    @PostMapping("/completed")
    public void completed(@RequestBody GoalCompletedDto goalCompletedDto) {
        eventPublisher.publishToTopic(RedisTopics.GOAL_COMPLETED.getTopicName(), goalCompletedDto);
    }
}
