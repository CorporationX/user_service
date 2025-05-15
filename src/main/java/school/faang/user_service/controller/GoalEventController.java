package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.GoalCompletedEvent;
import school.faang.user_service.event.GoalCompletedEventPublisher;

@Slf4j
@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalEventController {

    private final GoalCompletedEventPublisher publisher;

    @PostMapping("/complete")
    public String completeGoal(@RequestBody GoalCompletedEvent request) {
        publisher.publish(request);
        log.info("GoalCompletedEvent published for userId={}, goalId={}", request.getUserId(), request.getGoalId());
        return "GoalCompletedEvent published for userId=" + request.getUserId() + ", goalId=" + request.getGoalId();
    }
}