package school.faang.user_service.controller.goal;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.goal.GoalService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/goal")
public class GoalController {
    private final GoalService goalService;

    @GetMapping("/get/{userId}")
    public ResponseEntity<?> getGoalsByUser(@PathVariable("userId") Long userId,
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

    @GetMapping("/get/subtasks/{goalId}")
    public ResponseEntity<?> findSubtasksByGoalId(@PathVariable("goalId") Long goalId,
                                                  @RequestBody(required = false) GoalFilterDto filter) {
        try {
            return ResponseEntity.ok(goalService.findSubtasksByGoalId(goalId, filter));
        } catch (DataValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

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

    @DeleteMapping("/delete/{goalId}")
    public ResponseEntity<?> deleteGoal(@PathVariable("goalId") Long goalId) {
        try {
            goalService.deleteGoal(goalId);
            return ResponseEntity.ok().build();
        } catch (DataValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
