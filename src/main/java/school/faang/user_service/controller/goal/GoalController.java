package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class GoalController {
    private final GoalService goalService;

    @PutMapping("/{goalId}/complete")
    public ResponseEntity<GoalDto> completeGoal(@PathVariable Long goalId,
                                                @RequestParam Long userId
    ) {
        GoalDto completedGoal = goalService.completeGoal(goalId, userId);

        log.info("Goal completed successfully: goalId={}, userId={}", goalId, userId);
        return ResponseEntity.ok(completedGoal);
    }
}
