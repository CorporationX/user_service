package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalResponse;
import school.faang.user_service.exception.BusinessException;
import school.faang.user_service.service.goal.GoalServiceImpl;
import school.faang.user_service.util.Helper;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/goals")
public class GoalController {
    private final GoalServiceImpl goalService;

    //todo не сделаны эндпоинты: получения целей + фильтры; завершения цели

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@RequestBody GoalCreateDto goalCreateRq) {
        return response(() -> goalService.createGoal(goalCreateRq), HttpStatus.CREATED);
    }

    @PutMapping(path = "/{goalId}")
    public ResponseEntity<GoalResponse> updateGoalHandler(@PathVariable Long goalId, @RequestBody GoalDto goalUpdateRq) {
        return response(() -> goalService.updateGoal(goalId, goalUpdateRq), HttpStatus.ACCEPTED);
    }

    @DeleteMapping(path = "/{goalId}")
    public ResponseEntity<GoalResponse> deleteGoalHandler(@PathVariable Long goalId) {
        return response(() -> goalService.deleteGoal(goalId), HttpStatus.OK);
    }

    private ResponseEntity<GoalResponse> response(Helper.ThrowingSupplier<GoalResponse, BusinessException> sup, HttpStatus status) {
        return ResponseEntity.status(status).body(sup.get());
    }
}
