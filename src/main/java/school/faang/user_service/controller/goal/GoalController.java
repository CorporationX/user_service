package school.faang.user_service.controller.goal;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/goal")
public class GoalController {
    private final GoalService goalService;

    @PostMapping("/{userId}/create-goal")
    public ResponseEntity<?> createGoal(@PathVariable("userId") Long userId,
                                        @RequestBody GoalDto goalDto) {
        try {
            return ResponseEntity.ok(goalService.createGoal(userId, goalDto));
        } catch (DataValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{goalId}")
    public ResponseEntity<?> updateGoal(@PathVariable("goalId") Long goalId,
                                        @RequestBody GoalDto goalDto) {
        try {
            return ResponseEntity.ok(goalService.updateGoal(goalId, goalDto));
        } catch (DataValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getGoalsByUser(@PathVariable Long userId,
                                            @RequestBody(required = false) GoalFilterDto filter) {
        try {
            return ResponseEntity.ok(goalService.getGoalsByUser(userId, filter));
        } catch (DataValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
